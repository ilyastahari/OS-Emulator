public class InstructionCache {
    private static final int CACHE_SIZE = 8; // Number of words in the cache
    private Word baseAddress; // Address represented by the first word in the cache
    private Word[] cache; // Cache storage
    private int cacheClockCycles = 10; // Clock cycles for cache hit
    private int mainMemoryClockCycles = 350; // Clock cycles for cache miss

    public InstructionCache() {
        cache = new Word[CACHE_SIZE];
    }

    /**
     * Checks if the address is within the current cache range.
     * @param address The address to check.
     * @return True if address is in the cache, false otherwise.
     */
    private boolean isAddressInCache(Word address) {
        long addressValue = address.getUnsigned();
        long baseAddressValue = baseAddress.getUnsigned();
        return addressValue >= baseAddressValue && addressValue < (baseAddressValue + CACHE_SIZE);
    }

    /**
     * Fills the cache from the main memory.
     * @param address The base address from where to fill the cache.
     */
    private void fillCache(Word address) {
        baseAddress = new Word(address);
        int startIndex = (int) address.getUnsigned();
        for (int i = 0; i < CACHE_SIZE; i++) {
            cache[i] = MainMemory.read(new Word(startIndex + i));
        }
    }

    /**
     * Reads a word from the cache or main memory.
     * @param address The address to read.
     * @return The word from the cache or main memory.
     */
    public Word read(Word address) {
        if (baseAddress == null || !isAddressInCache(address)) { // Cache miss
            fillCache(address);
            Processor.incrementClockCycles(mainMemoryClockCycles);
        } else { // Cache hit
            Processor.incrementClockCycles(cacheClockCycles);
        }

        int index = (int) (address.getUnsigned() - baseAddress.getUnsigned());
        return cache[index];
    }
}