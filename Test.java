import java.util.Arrays;
import mpi.*;

public class Test{
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
                        double sum = m.data[p].e * n.data[w].e;  
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
            ensureCapacity(tu + 1);   
            data[tu++] = triple;    //size++  
            return true;  
        }  
    }

    public static void main(String[] args) {

        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int tag1 = 1;
        int tag2 = 2;
        int tag3 = 3;
        int peer = (rank == 0)?1:0;
        if(rank == 0){
            Triple a1 = new Triple(1,2,1);
            Triple a2 = new Triple(2,1,1);
            Triple a3 = new Triple(3,1,-5);
            Triple b1 = new Triple(1,0,1);
            Triple b2 = new Triple(1,1,2);
            Triple b3 = new Triple(2,2,-1);

            Matrix test1 = new Matrix(0);
            test1.mu = 4;
            test1.nu = 3;
            test1.add(a1);
            test1.add(a2);
            test1.add(a3);

            Matrix test2 = new Matrix(0);
            test2.mu = 3;
            test2.nu = 4;
            test2.add(b1);
            test2.add(b2);
            test2.add(b3);

            MPI.COMM_WORLD.Send(test1, 0, test1.mu*test1.nu, MPI.DOUBLE, peer, tag1);
            MPI.COMM_WORLD.Send(test2, 0, test2.mu*test2.nu, MPI.DOUBLE, peer, tag2);
            System.out.println("I'm sending.");
        }else{

            Matrix test1 = new Matrix(0);
            test1.mu = 4;
            test1.nu = 3;
            Matrix test2 = new Matrix(0);
            test2.mu = 3;
            test2.nu = 4;
            MPI.COMM_WORLD.Recv(test1, 0, test1.mu*test1.nu, MPI.DOUBLE, peer, tag1);
            MPI.COMM_WORLD.Recv(test2, 0, test2.mu*test2.nu, MPI.DOUBLE, peer, tag2);
            System.out.println("I'm receving.");

        }    
    }
}