public class L2Cache {
    private static final int CACHE_GROUPS = 4; // Number of groups
    private static final int GROUP_SIZE = 8; // Number of words in a group
    private Word[] baseAddresses; // Base addresses for each group
    private Word[][] cache; // L2 cache groups
    private static final int L2_CACHE_CYCLE_COST = 20; // Clock cycles for accessing L2
    private static final int FILL_CACHE_CYCLE_COST = 350; // Clock cycles for filling L2

    public L2Cache() {
        baseAddresses = new Word[CACHE_GROUPS];
        cache = new Word[CACHE_GROUPS][GROUP_SIZE];
    }

    /**
     * Checks which group the address belongs to.
     * @param address The address to check.
     * @return The group index or -1 if not in any group.
     */
    private int getGroupIndex(Word address) {
        for (int i = 0; i < CACHE_GROUPS; i++) {
            if (baseAddresses[i] != null && isAddressInGroup(address, baseAddresses[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks if the address is within the specified group.
     * @param address The address to check.
     * @param baseAddress The base address of the group.
     * @return True if address is in the group, false otherwise.
     */
    private boolean isAddressInGroup(Word address, Word baseAddress) {
        long addressValue = address.getUnsigned();
        long baseAddressValue = baseAddress.getUnsigned();
        return addressValue >= baseAddressValue && addressValue < (baseAddressValue + GROUP_SIZE);
    }

    /**
     * Reads a word from the L2 cache, filling from main memory if not found.
     * @param address The address to read.
     * @return The word from the cache, or null if not found.
     */
    public Word read(Word address) {
        int groupIndex = getGroupIndex(address);

        if (groupIndex == -1) { // Cache miss
            groupIndex = fillFromMainMemory(address); // Fill L2 from main memory
            Processor.incrementClockCycles(FILL_CACHE_CYCLE_COST); // Fill cost
        } else {
            Processor.incrementClockCycles(L2_CACHE_CYCLE_COST); // Cache hit cost
        }

        int index = (int) (address.getUnsigned() - baseAddresses[groupIndex].getUnsigned());
        return cache[groupIndex][index];
    }

    /**
     * Fills the L2 cache from main memory, given a base address.
     * @param baseAddress The base address to start filling from.
     * @return The index of the filled group.
     */
    public int fillFromMainMemory(Word baseAddress) {
        int groupIndex = findEmptyGroup(); // Find an empty group
        if (groupIndex == -1) {
            groupIndex = 0; // If no empty group, replace the first one (can be enhanced to use LRU or other strategies)
        }

        fillGroup(groupIndex, baseAddress); // Fill the group from main memory
        return groupIndex;
    }

    /**
     * Fills the specified group with data from the main memory.
     * @param groupIndex The index of the group to fill.
     * @param baseAddress The base address for the group.
     */
    private void fillGroup(int groupIndex, Word baseAddress) {
        this.baseAddresses[groupIndex] = new Word(baseAddress); // Set the base address
        int start = (int) baseAddress.getUnsigned();

        for (int i = 0; i < GROUP_SIZE; i++) {
            cache[groupIndex][i] = MainMemory.read(new Word(start + i));
        }
    }

    /**
     * Finds the index of an empty group in the L2 cache.
     * @return The index of the empty group, or -1 if all groups are full.
     */
    private int findEmptyGroup() {
        for (int i = 0; i < CACHE_GROUPS; i++) {
            if (baseAddresses[i] == null) {
                return i;
            }
        }
        return -1;
    }
}