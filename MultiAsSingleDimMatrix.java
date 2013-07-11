/*
 * created by Jinyang
 * summer 2013
 */

import mpi.*;
public class MultiAsSingleDimMatrix{
	public static final int N = 10;

	public static void display(double[][] matrix, int row){
		for(int i = 0; i<row; i++){
			System.out.println(matrix[i][0]+" "+matrix[i][1]+" "+matrix[i][2]);
		}
	}
	
	public static void main(String[] args) {
		MPI.Init(args);
		int[] all = new int[2];
		int rank = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		int tag = 10;
		System.out.println("rank is "+rank);
		System.out.println("size is "+size);
		if(rank == 0){
			int[] a = new int[2];
			a[0] = 1;
			a[1] = 1;
			for(int p=1; p<4; p++)
				MPI.COMM_WORLD.Send(a, 0, 2, MPI.INT, p, tag);
			System.out.println("I'm sending");
			MPI.COMM_WORLD.Reduce(a, 0, all, 0, 2, MPI.INT, MPI.SUM, 0);
			System.out.println("result is "+all[0]+all[1]);
		}else{
			int[] b = new int [2];

			MPI.COMM_WORLD.Recv(b, 0, 2, MPI.INT, 0, tag);
			System.out.println("Ive received "+b[0]+" "+b[1]);
			MPI.COMM_WORLD.Reduce(b, 0, all, 0, 2, MPI.INT, MPI.SUM, 0);
			System.out.println("reduced");
		}
		MPI.Finalize();
	}
}