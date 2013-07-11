import java.util.Arrays;
import java.io.*;
import java.util.*;
import mpi.*;

public class Test{
    private static Random random = new Random();
	public static void display(double[][] matrix, int row){
		for(int i = 0; i<row; i++){
			System.out.println(matrix[i][0]+" "+matrix[i][1]+" "+matrix[i][2]);
		}
	}
	public static double[][] convert(double[][] A, int row, int col){
		double[][] B = new double[5][3];
		int index = 0;
		for(int i=0; i<row; i++){
			for(int j=0; j<col; j++){
				if(A[i][j] != 0){
					B[index][0] = i;
					B[index][1] = j;
					B[index][2] = A[i][j];
					index++;
				}
			}
		}
		return B;
	}

    public static Matrix multi(Matrix m, Matrix n){
        Matrix q = new Matrix(0);
        q.mu = m.mu;
        q.nu = n.nu;

        int[] mNum = new int[m.mu];
        for(int len=0; len<m.tu; len++){
            mNum[m.data[len].i]++;
        }
        m.rpos = new int[m.mu];
        m.rpos[0] = 0;
        for(int mRow = 1; mRow<m.mu; mRow++){
            m.rpos[mRow] = m.rpos[mRow-1] + mNum[mRow-1];
        }
        int[] nNum = new int[n.mu];
        for(int len=0; len<n.tu; len++){
            nNum[n.data[len].i]++;
        }
        n.rpos = new int[n.mu];
        n.rpos[0] = 0;
        for(int nRow=1; nRow<n.mu; nRow++){
            n.rpos[nRow] = n.rpos[nRow-1] + nNum[nRow-1];
        }
        if(m.tu * n.tu !=0){
            for(int arow=0;arow<m.mu;arow++){
                int mlast=0;
                if(arow < m.mu-1)
                    mlast = m.rpos[arow+1];
                else
                    mlast = m.tu;
                for(int p=m.rpos[arow];p<mlast;p++){
                    int brow = m.data[p].j;
                    int nlast=0;
                    if(brow < n.mu-1)
                        nlast = n.rpos[brow+1];
                    else
                        nlast = n.tu;
                    for(int w=n.rpos[brow];w<nlast;w++){
                        int ccol = n.data[w].j;
                        int sum = m.data[p].e * n.data[w].e;  
                        if(sum!=0){
                            Triple triple = new Triple(arow, ccol , sum);
                            q.add(triple);
                        }
                    }
                }
            }
        }
        return q;
    }

    static class Triple{
        int i;
        int j;
        int e;
        Triple(int i, int j, int e){
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
        int[] number = new int[2];
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int tagN = 1;
        int tag1_1 = 11;
        int tag2_1 = 21;
        // adjust the result triple array.
        int[] temp22 = new int[600];
        if(rank == 0){
            Matrix test1 = new Matrix(0);
            test1.mu = 200;
            test1.nu = 200;
            for (int n = 0; n < 100; n++){
                Triple a = new Triple(random.nextInt(test1.mu), random.nextInt(test1.nu), random.nextInt(10));
                test1.add(a);
            }
            Matrix test2 = new Matrix(0);
            test2.mu = 200;
            test2.nu = 200;
            for (int n = 0; n < 100; n++){
                test2.add(new Triple(random.nextInt(test2.mu), random.nextInt(test2.nu), random.nextInt(10)));
            }
            Matrix test3 = new Matrix(0);
            test3.mu = test1.mu;
            test3.nu = test2.nu;

            Matrix testEach = new Matrix(0);
            for(int xx=1; xx<Integer.parseInt(args[1]); xx++){
                testEach = test1.split(Integer.parseInt(args[1])-1, xx);
                number[0] = testEach.tu;
                number[1] = test2.tu;
                System.out.println("before"+number[0]+" "+number[1]);
                MPI.COMM_WORLD.Send(number, 0, 2, MPI.INT, xx, tagN);
            }
            
            long timeBegin = System.currentTimeMillis();
            
            for(int x=1; x<Integer.parseInt(args[1]); x++){
                testEach = test1.split(Integer.parseInt(args[1])-1, x);
                for(int y = 0; y<testEach.tu; y++){
                    int[] temp = new int[3];
                    temp[0] = testEach.data[y].i;
                    temp[1] = testEach.data[y].j;
                    temp[2] = testEach.data[y].e;
                    MPI.COMM_WORLD.Send(temp, 0, 3, MPI.INT, x, tag1_1);
                }
            }

            for(int x=0; x<test2.tu; x++){
                int[] temp = new int[3];
                temp[0] = test2.data[x].i;
                temp[1] = test2.data[x].j;
                temp[2] = test2.data[x].e;
                for(int sendRank2=1; sendRank2<Integer.parseInt(args[1]); sendRank2++)
                    MPI.COMM_WORLD.Send(temp, 0, 3, MPI.INT, sendRank2, tag2_1);
            }
            /*
            Matrix tempM = new Matrix(0);
            tempM = multi(test1, test2);
            System.out.println(tempM.mu);
            for(int x=0; x<tempM.tu; x++){
                int[] temp11 = new int[3];
                int[] temp22 = new int[3];
                temp11[0] = tempM.data[x].i;
                temp11[1] = tempM.data[x].j;
                temp11[2] = tempM.data[x].e;
                tempM.data[x].display();
                MPI.COMM_WORLD.Reduce(temp11, 0, temp22[rank], 0, 3, MPI.INT, MPI.SUM, 0);
            }*/
            int[] temp11 = new int[600];
            MPI.COMM_WORLD.Reduce(temp11, 0, temp22, 0, 600, MPI.INT, MPI.SUM, 0);
            //MPI.COMM_WORLD.Barrier();
            System.out.println("This program use "+(System.currentTimeMillis()-timeBegin)/1000f + " s");
            System.out.println("reduce's result is ");
            for(int i=0; i<200; i+=3){
                System.out.println(temp22[i]+" "+temp22[i+1]+" "+temp22[i+2]);
            }
        }else{
            Matrix testEachR = new Matrix(0);
            testEachR.mu = 200;
            testEachR.nu = 200;
            Matrix test2 = new Matrix(0);
            test2.mu = 200;
            test2.nu = 200;
            int[] numberR = new int[2];
            MPI.COMM_WORLD.Recv(numberR, 0, 2, MPI.INT, 0, tagN);

            for(int x=0; x<numberR[0]; x++){
                int[] temp = new int[3];
                MPI.COMM_WORLD.Recv(temp, 0, 3, MPI.INT, 0, tag1_1);
                System.out.println("Test 1 receiving "+rank);
                testEachR.add(new Triple(temp[0],temp[1],temp[2]));
                System.out.println("added");
            }
            System.out.println("Test 1 receving finished."+rank);

            for(int x=0; x<numberR[1]; x++){
                int[] temp = new int[3];
                System.out.println("Test 2 receiving"+rank);
                MPI.COMM_WORLD.Recv(temp, 0, 3, MPI.INT, 0, tag2_1);
                test2.add(new Triple(temp[0], temp[1], temp[2]));
            }
            System.out.println("Test 2 receiving finished."+rank);
            Matrix tempM = new Matrix(0);
            //testEachR.display();
            //test2.display();
            tempM = multi(testEachR, test2);
            System.out.println(tempM.mu+" "+tempM.nu+" "+tempM.tu+" "+rank);
            System.out.println("multi ok "+rank);

            int[] temp11 = new int[600];
            for(int x=0; x<tempM.tu; x++){
                temp11[(rank-1)*100+3*x+1] = tempM.data[x].i;
                temp11[(rank-1)*100+3*x+2] = tempM.data[x].j;
                temp11[(rank-1)*100+3*x+3] = tempM.data[x].e;
                System.out.print(rank);
                tempM.data[x].display();
            }
            MPI.COMM_WORLD.Reduce(temp11, 0, temp22, 0, 600, MPI.INT, MPI.SUM, 0);
        }
        MPI.Finalize();
    }
}