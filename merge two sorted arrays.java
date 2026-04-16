// Online Java Compiler
// Use this editor to write, compile and run your Java code online
import java.util.*;
class Main {
    public static void main(String[] args) {
    
    int arr1[]={1,3,5};
    int arr2[]={2,4,6,7};
    int result[]=new int[arr1.length+arr2.length];
    int i=0;
    int j=0;
    int index=0;
    
    while(i<arr1.length&& j<arr2.length)
    {
        
    if(arr1[i]<arr2[j])
    {
        result[index++]= arr1[i++];
      
    }else{
         result[index++]= arr2[j++];
    }
        
    }
    
    while(i<arr1.length)
    {
        result[index++]= arr1[i++];
    }
    
      while(j<arr2.length)
    {
        result[index++]= arr2[j++];
    }
    
    System.out.println(Arrays.toString(result));
 
    
    
    }
}
