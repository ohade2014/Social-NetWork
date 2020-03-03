package com.company;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {


    public static int [] Sorted_merge1(int []a, int [] b)
    {
        int ia=0;
        int ib=0;
        int it=0;
        int [] buffer=new int [a.length+b.length];
        while(ia<a.length && ib<b.length)
        {
            if(a[ia]<b[ib]) {
                buffer[it] = a[ia];
                ia++;
            }
            else {
                buffer[it] = b[ib];
                ib++;
            }
            it++;
        }
        if(ia<a.length)
            while (ia<a.length)
            {
                buffer[it]=a[ia];
                ia++;
                it++;
            }
        else
            while (ib<b.length)
            {
                buffer[it]=b[ib];
                ib++;
                it++;
            }
            return buffer;
    }

    public static  void Sorted_merge2(int [] A, int [] B, int lastA,int lastB){
        int indexa=lastA-1;
        int indexb=lastB-1;
        int index_last=lastA+lastB-1;
        while(indexb>=0)
        {
            if(indexa>=0 && A[indexa]>B[indexb])
            {
                A[index_last]=A[indexa];
                indexa--;
            }
            else {
                A[index_last]=B[indexb];
                indexb--;
            }
            index_last--;
        }
    }
    public static void group_anagrans(String[]arr){
        HashMap<String,HashSet<String>> str=new HashMap<>();
        for(int i=0;i<arr.length;i++) {
            char[] temp = arr[i].toCharArray();
            int[] num_of_char = new int[26];
            for (int j = 0; j < temp.length; j++) {
                num_of_char[temp[j]]++;
            }
            int cou = 0;
            for (int t = 0; t < 26; t++) {
                while (num_of_char[t] > 0) {
                    temp[cou] = (char) (t + 'a');
                    cou++;
                    num_of_char[t]--;
                }
            }
            String st = new String(temp);
            if (str.get(st) == null)
                str.put(st, new HashSet<String>());
            str.get(st).add(arr[i]);
        }
        }

    public static int search_in_rotated_array(int [] arr, int num){
        int left=0;
        int right=arr.length-1;
        int med=-1;
        while(left<right)
        {
            med=(left+right)/2;
            if(arr[med]==num)
                return med;
            else
            {
                if(arr[left]<num & arr[med]>=num)
                    right=med;
                else if (arr[med]<=num & arr[right]>num)
                    left=med;
                else if(arr[left]>=num & arr[med]>num & arr[left]>arr[med])
                    right=med;
                else if (arr[med]>num  & arr[right]>=num & arr[med]>arr[right])
                    left=med;
            }
        }
        if(arr[med]==num)
            return med;
        else
            return -1;
    }
    public static void main(String[] args) {
      /*  int [] a= new  int[12];
        a[0]=1;
        a[1]=3;
        a[2]=23;
        a[3]=56;
        a[4]=100;
	    int [] b= {0,4,23,1340, 53555, 10000000};
	    //int [] res=Sorted_merge1(a,b);
        Sorted_merge2(a,b,5,6);
	    for(int i=0;i<a.length;i++)
	        System.out.println(a[i]);
	    String[] arr= {"dog","abc","god","cab","arr"};
	    group_anagrans(arr);
	    for (int i=0;i<arr.length;i++)
        {
            System.out.println(arr[i]);
        }*/
        int [] c={14,15,16,19,20,25,1,3,4,5,7,10};
        //int [] c={15,16,19,20,25,1,3,4,5,7,10,14};
	    System.out.println(search_in_rotated_array(c,5));

    }}
