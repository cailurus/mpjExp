import java.util.Arrays;

public class MatrixDemo<E> {
	
	public static void main(String[] args) {
		MatrixDemo<Integer> md = new MatrixDemo<Integer>();

	    //md.compreMatrix(M);
	    Matrix m = new Matrix(3);
	    m.mu = 4;
	    m.nu = 3;
	    m.tu = 3;
	    
	    Matrix n = new Matrix(3);
	    n.mu = 3;
	    n.nu = 4;
	    n.tu = 3;

	    md.multiMatrix(m, n);
	    System.out.println();
	}
	

	public Matrix compreMatrix(E[][] e){
		Matrix matrix = new Matrix();
		int mu = e.length;
		int nu = e[0].length;
		for(int i=0;i<mu;i++){
			for(int j=0;j<nu;j++){
				if(e[i][j] != null){
					Triple<E> triple = new Triple<E>(i,j,e[i][j]);
					matrix.add(triple);
				}
			}
		}
		matrix.mu = mu;
		matrix.nu = nu;
		

		for(int i=0;i<matrix.tu;i++){
			Triple<E> t = matrix.data[i];
			System.out.print(t.i + " " + t.j + " " + t.e);
			System.out.println();
		}
		System.out.println("----------");
//		transMatrix(matrix);
//		transMatrix2(matrix);
		return matrix;
	}
	public Matrix multiMatrix(Matrix m,Matrix n){
		Matrix q = new Matrix();
		q.mu = m.mu;
		q.nu = n.nu;

		int[] mNum = new int[m.mu];
		for(int len=0;len<m.tu;len++){
			mNum[m.data[len].i]++;
		}
		m.rpos = new int[m.mu];
		m.rpos[0] = 0;
		for(int mRow=1;mRow<m.mu;mRow++){
			m.rpos[mRow] = m.rpos[mRow-1] + mNum[mRow-1];
		}
		int[] nNum = new int[n.mu];
		for(int len=0;len<n.tu;len++){
			nNum[n.data[len].i]++;
		}
		n.rpos = new int[n.mu];
		n.rpos[0] = 0;
		for(int nRow=1;nRow<n.mu;nRow++){
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
						int sum = (Integer)m.data[p].e * (Integer)n.data[w].e;	
						if(sum!=0){
							Triple<Integer> triple = new Triple<Integer>(arow, ccol , sum);
							q.add(triple);
						}
					}
				}
			}
		}

		for(int i=0;i<q.tu;i++){
			Triple<E> tr = q.data[i];
			System.out.print(tr.i + " " + tr.j + " " + tr.e);
			System.out.println();
		}
		return q;
	}
	
	public static class Triple<E>{
		int i;
		int j;
		E e;
		Triple(int i,int j,E e) {
			this.i = i;
			this.j = j;
			this.e = e;
		}
	}

	public static class Matrix{
		Triple[] data;
		int mu;
		int nu;
		int tu;
		int[] rpos;
		public Matrix(){
			this(10);
		}
		
		public Matrix(int capacity) {
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
			data[tu++] = triple;	//size++
			return true;
		}
		
		public boolean add(int index,Triple triple){
			if (index >= tu || index < 0)
				throw new IndexOutOfBoundsException("Index: " + index + ", Size: "+ tu);
			ensureCapacity(tu + 1); 
			data[index] = triple;	
			tu++;
			return true;
		}
	}
	
	
}
