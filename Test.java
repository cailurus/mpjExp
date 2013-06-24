public class Test{
	public static void display(double[][] matrix, int row){
		for(int i = 0; i<row; i++){
			System.out.println(matrix[i][0]+" "+matrix[i][1]+" "+matrix[i][2]);
		}
	}
	public static void main(String[] args) {
		System.out.println("ss");
		double[][] a = new double[2][3];
		for(int i=0; i<2; i++){
			for(int j=0; j<3; j++)
				a[i][j] = 5;
		}
		display(a, 2);

	}
}