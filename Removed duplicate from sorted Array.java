// Online Java Compiler
// Use this editor to write, compile and run your Java code online
import java.util.*;
class Main {
    public static void main(String[] args) {
    
   int arr[] = {1, 2, 2, 3, 4, 4, 4, 5};
   int index=0;
   int i=1;
   while(i<arr.length)
   {
       if(arr[i]==arr[i-1])
       {
           i++;
       }else{
           
           arr[index+1]=arr[i++];
           index++;
       }
       
   }
   
  for(int k=0;k<=index;k++)
  {
      System.out.print(arr[k]+" ");
  }

    
    
    }
}
