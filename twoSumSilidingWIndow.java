// Online Java Compiler
// Use this editor to write, compile and run your Java code online

class Main {
    public static void main(String[] args) {
    int arr[] = {1, 2, 3, 7, 5};
   
    
    
   int target  =12;
//Output : Subarray = [2, 3, 7]
    int j=0;
    int sum=0;
    for(int i=0;i<arr.length;i++)
    {
         sum=sum+arr[i];
         while(sum>target)
         {
             sum=sum-arr[j];
             j++;
         }
        
         if(sum==target)
         {
            System.out.print("[");
            for(int k=j;k<=i;k++)
            {
                System.out.print(arr[k]);
                if(k<i)
                {
                    System.out.print(",");
                }
            }
            System.out.print("]");
           return; 
         }
         
    }

 
    }
}
