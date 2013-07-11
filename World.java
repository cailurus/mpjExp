public class World{
	public static void main(String args[]){
		int[] yy = new int[10];
		for( int i = 0; i<yy.length; i++)
			yy[i] += 1;
		System.out.println(yy[9]);

		int[][] xx  = new int[2][10];
		xx[1] = yy;
		System.out.println(xx[1][2]);
	}
}