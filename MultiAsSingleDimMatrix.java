/*
 * created by Jinyang
 * summer 2013
 */

import mpi.*;
public class MultiAsSingleDimMatrix{
	public static final int N = 10;
	public static void main(String[] args) {
		MPI.Init(args);

		int rank = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		int tag = 10;
		int peer = (rank == 0)? 1:0;
		System.out.println("rank is "+rank);
		System.out.println("size is "+size);

		if(rank == 0){
			double [] a = new double [N*N];

			for(int i=0; i<N; i++)
				for(int j=0; j<N; j++)
					a[(N*i)+j] = 10.0;

			MPI.COMM_WORLD.Send(a, 0, N*N, MPI.DOUBLE, peer, tag);
			System.out.println("I'm sending");

		}else if(rank == 1){
			double [] b = new double [N*N];
			for(int i = 0; i<N; i++)
				for(int j = 0; j<N; j++)
					b[(N*i)+j] = 0;
			MPI.COMM_WORLD.Recv(b, 0, N*N, MPI.DOUBLE, peer, tag);
			System.out.println("I'm receving");
			for(int i = 0; i<4; i++){
				for(int j = 0; j<N; j++)
					System.out.println(b[(N*i)+j]+"this is "+(N*i+j));
				System.out.println("\n");
			}
		}
		MPI.Finalize();
	}
}