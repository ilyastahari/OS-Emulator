public class StringHandler {


   /**private string to hold our assembly file */
   private String assembly_file;

   /**this is the finger/cursor position */
   private int index;

   public StringHandler(String awk){
        this.assembly_file = awk;
        index = 0;
   }

    /**looks i characters ahead and returns character while not moving index */
    public char Peek(int i) {
        return assembly_file.charAt(index+i);
    }

    /**returns characters as a string and doesn't move index */
    public String PeekString(int i){
       return assembly_file.substring(index, index+i);
    }

    /**returns the next character and increments index */
    public char GetChar(){
       char c = assembly_file.charAt(index);
       index++;
       return c;
    }

    /**moves the index forward i positions */
    public void Swallow(int i){
       index = index + i;
    }

    /**method which returns true only if at end of doc */
    public boolean isDone(){
       int file_length = assembly_file.length();
       if (index >= file_length){
           return true;
       }
       return false;

    }

    /**method which returns the rest of doc as a string */
    public String Remainder(){
        return assembly_file.substring(index);
    }
}
