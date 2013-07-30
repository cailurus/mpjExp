import java.io.*;
import java.util.*;

public class TestI {
	private static Random random = new Random();

    static class Triple{
        int i;
        int j;
        //int e;
        double e;
        Triple(int i, int j, double e){
            this.i = i;
            this.j = j;
            this.e = e;
        }
        public void display(){
            System.out.println("this triple is "+this.i +" "+this.j+" "+this.e+" .");
        }
    }
    
    static class Matrix{
        Triple[] data;
        int mu;
        int nu;
        int tu;
        int[] rpos;
        public Matrix(int capacity){
            data = new Triple[capacity];
        }
        public void ensureCapacity(int minCapacity) {  
            int oldCapacity = data.length;  
            if (minCapacity > oldCapacity) { 
                int newCapacity = (oldCapacity * 3) / 2 + 1;
                if (newCapacity < minCapacity)  
                    newCapacity = minCapacity;  
                data = Arrays.copyOf(data, newCapacity);  
            }  
        }  
        public boolean add(Triple triple){
            data = Arrays.copyOf(data, data.length+1);
            data[data.length-1] = triple;
            tu ++;
            return true;
        }
        public void display(){
            System.out.println("mu is "+this.mu+", nu is "+this.nu+", tu is "+this.tu);
            for(Triple cc : this.data)
                cc.display();
        }
        public Matrix split(int all, int which){
            Matrix newOne = new Matrix(0);
            int scanBegin = (this.mu/all) * (which-1);
            int scanEnd = (this.mu/all) * which;
            for(int x=0; x<this.data.length; x++){
                if(scanBegin < data[x].i && data[x].i <= scanEnd){
                    newOne.add(data[x]);
                }
            }
            return newOne;
        }
    }

	public static void main(String[] args) {
		Matrix test1 = new Matrix(0);
		test1.mu = 20;
		test1.nu = 20;

		Matrix test2 = new Matrix(0);
		test2.mu = 20;
		test2.nu = 20;

		File dataFile1 = new File("/Users/jinyangzhou/Desktop/testMatrix1");
		File dataFile2 = new File("/Users/jinyangzhou/Desktop/testMatrix2");

		try{
			PrintWriter pw1 = new PrintWriter(dataFile1);
			for (int n = 0; n < 100; n++){
				Triple a = new Triple(random.nextInt(test1.mu), random.nextInt(test1.nu), random.nextDouble()*10);
				pw1.write(a.i+" "+a.j+" "+a.e+'\n');
			}
			pw1.close();
			PrintWriter pw2 = new PrintWriter(dataFile2);
			for (int n = 0; n < 100; n++){
				Triple b = new Triple(random.nextInt(test2.mu), random.nextInt(test2.nu), random.nextDouble()*10);
				pw2.write(b.i+" "+b.j+" "+b.e+'\n');
			}
			pw2.close();
		}catch(FileNotFoundException e){
			System.out.println("can't find the file, error: "+e.getMessage());
		}
	}

}