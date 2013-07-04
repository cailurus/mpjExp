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
        Matrix q = new Matrix(10);
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
        for(int xx : mNum){
            System.out.println(xx);
        }
        System.out.println("---");
        for(int yy : m.rpos){
            System.out.println(yy);
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
            ensureCapacity(tu + 1);   
            data[tu++] = triple;    //size++  
            return true;  
        }  
    }

    public static void main(String[] args) {
        int numberA = 0;
        int numberB = 0;
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int tag1_1 = 11;
        int tag2_1 = 21;
        int peer = (rank == 0)?1:0;
        if(rank == 0){
            Matrix test1 = new Matrix(0);
            test1.mu = 20;
            test1.nu = 20;
            for (int n = 0; n < 400; n++){
                Triple a = new Triple(random.nextInt(20), random.nextInt(20), random.nextInt(1000));
                test1.add(a);
                numberA += 1;
            }

            Matrix test2 = new Matrix(0);
            test2.mu = 20;
            test2.nu = 20;
            for (int n = 0; n < 400; n++){
                test2.add(new Triple(random.nextInt(20), random.nextInt(20), random.nextInt(1000)));
                numberB += 1;
            }

            Matrix test3 = new Matrix(0);
            test3.mu = test1.mu;
            test3.nu = test2.nu;
            for(int x=0; x<test1.tu; x++){
                int[] temp = new int[3];
                temp[0] = test1.data[x].i;
                temp[1] = test1.data[x].j;
                temp[2] = test1.data[x].e;
                MPI.COMM_WORLD.Send(temp, 0, 3, MPI.INT, peer, tag1_1);
            }

            for(int x=0; x<test2.tu; x++){
                int[] temp = new int[3];
                temp[0] = test2.data[x].i;
                temp[1] = test2.data[x].j;
                temp[2] = test2.data[x].e;
                MPI.COMM_WORLD.Send(temp, 0, 3, MPI.INT, peer, tag2_1);
            }

            System.out.println("I'm sending. 0");
            Matrix tempM = new Matrix(0);
            System.out.print("sss");
            tempM = multi(test1, test2);
            System.out.println(tempM.mu);
            for(int x=0; x<tempM.tu; x++){
                int[] temp11 = new int[3];
                int[] temp22 = new int[3];
                temp11[0] = tempM.data[x].i;
                temp11[1] = tempM.data[x].j;
                temp11[2] = tempM.data[x].e;
                tempM.data[x].display();
                MPI.COMM_WORLD.Reduce(temp11, 0, temp22, 0, 3, MPI.INT, MPI.SUM, 0);
            }
            
        }else{
            Matrix test1 = new Matrix(0);
            test1.mu = 20;
            test1.nu = 20;
            Matrix test2 = new Matrix(0);
            test2.mu = 20;
            test2.nu = 20;

            for(int x=0; x<numberA; x++){
                int[] temp = new int[3];
                System.out.println("init finished");
                MPI.COMM_WORLD.Recv(temp, 0, 3, MPI.INT, peer, tag1_1);
                System.out.println("received ..");
                test1.add(new Triple(temp[0],temp[1],temp[2]));
                System.out.println("added");
            }
            for(int x=0; x<numberB; x++){
                int[] temp = new int[3];
                MPI.COMM_WORLD.Recv(temp, 0, 3, MPI.INT, peer, tag2_1);
                test2.add(new Triple(temp[0], temp[1], temp[2]));
            }
            
            System.out.println("I'm receving.");
            Matrix tempM = new Matrix(0);
            tempM = multi(test1, test2);

            for(int x=0; x<tempM.tu; x++){
                int[] temp11 = new int[3];
                int[] temp22 = new int[3];
                temp11[0] = tempM.data[x].i;
                temp11[1] = tempM.data[x].j;
                temp11[2] = tempM.data[x].e;
                tempM.data[x].display();
                MPI.COMM_WORLD.Reduce(temp11, 0, temp22, 0, 3, MPI.INT, MPI.SUM, 0);
            }
        }
        System.out.println("end");
        MPI.Finalize();
    }
}