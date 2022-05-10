import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class chan {
	
	static int[][] room;
	static int answer, N, M;
	static Cctv[] cctv;
	static ArrayList<Cam> place;
	static int[] dx = {0, -1, 0, 1};
	static int[] dy = {-1, 0, 1, 0};
	public static void main(String[] args) throws Exception {
		
		// cctv 정보 입력
		setCctvs();
		place = new ArrayList<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		answer = 64;	// 사각지대의 최소 크기
		N = Integer.parseInt(st.nextToken());	// 세로 크기
		M = Integer.parseInt(st.nextToken());	// 가로 크기
		room = new int[N][M];
		for (int n = 0; n < N; n++) {
			st = new StringTokenizer(br.readLine());
			for (int m = 0; m < M; m++) {
				room[n][m] = Integer.parseInt(st.nextToken());
				if (room[n][m] >= 1 && room[n][m] <= 5) place.add(new Cam(room[n][m], n, m));
			}
		}
		
		int[][] copyRoom = new int[N][M];
		copyRoom = copyRoom(copyRoom, room);
		solve(0, 0, copyRoom);
		
		System.out.println(answer);
	} 
	

	private static int[][] copyRoom(int[][] copyRoom, int[][] room) {
		for (int n = 0; n < N; n++) {
			for (int m = 0; m < M; m++) {
				copyRoom[n][m] = room[n][m]; 
			}
		}
		
		return copyRoom;
	}

	private static void solve(int cnt, int start, int[][] room) {
		if (cnt == place.size()) {
			int cntSpot = countBlindSpot(room);
			answer = answer > cntSpot ? cntSpot : answer;
			return;
		}
		
		for (int i = start; i < place.size(); i++) {
			Cam curCam = place.get(i);
			int[][] dir = cctv[curCam.num].direction;
			for (int j = 0; j < dir.length; j++) {
				boolean[][] visited = new boolean[N][M];
				watching(dir[j], curCam.x, curCam.y, room, visited);
				solve(cnt + 1, i + 1, room);
				// 이번 경우의 수에서 감시당한 구역만 원상복귀하기
				cancel(dir[j], curCam.x, curCam.y, room, visited);
			}
		}
	}


	// 사각지대(빈칸)인 수 세기
	private static int countBlindSpot(int[][] room) {
		int cnt = 0;
		for (int n = 0; n < N; n++) {
			for (int m = 0; m < M; m++) {
				if (room[n][m] == 0) cnt++;
			}
		}
		return cnt;
	}


	// 감시했다는 표시 없애기 (다음 경우의 수를 위해)
	private static void cancel(int[] dir, int x, int y, int[][] copyRoom, boolean[][] visited) {
		for (int i = 0; i < dir.length; i++) {
			int tx = x;
			int ty = y;

			while (true) {
				tx += dx[dir[i]];
				ty += dy[dir[i]];
				if (tx < 0 || ty < 0 || tx >= N || ty >= M) break;
				if (copyRoom[tx][ty] == 6) break;
				if (visited[tx][ty])
					copyRoom[tx][ty] = room[tx][ty];
			}
		}		
	}


	private static void watching(int[] dir, int x, int y, int[][] copyRoom, boolean[][] visited) {
		for (int i = 0; i < dir.length; i++) {
			int tx = x;
			int ty = y;

			while (true) {
				tx += dx[dir[i]];
				ty += dy[dir[i]];
				if (tx < 0 || ty < 0 || tx >= N || ty >= M) break;
				if (copyRoom[tx][ty] == 6) break;
				if (copyRoom[tx][ty] != 0) continue;
				if (copyRoom[tx][ty] == 7) continue;
				copyRoom[tx][ty] = 7;
				visited[tx][ty] = true;
			}
		}
	}

	private static void setCctvs() {
		cctv = new Cctv[6];
		cctv[1] = new Cctv(new int[][] {{0}, {1}, {2}, {3}});
		cctv[2] = new Cctv(new int[][] {{0,2}, {1,3}});
		cctv[3] = new Cctv(new int[][] {{0,1}, {1,2}, {2,3}, {0, 3}});
		cctv[4] = new Cctv(new int[][] {{0,2,3}, {0,1,3}, {0,1,2}, {1,2,3}});
		cctv[5] = new Cctv(new int[][] {{0,1,2,3}});
		
	}

	static class Cctv {
		int[][] direction;
		public Cctv(int[][] direction) {
			this.direction = direction;
		}
	}
	
	static class Cam {
		int num;
		int x; int y;
		public Cam(int num, int x, int y) {
			this.num = num;
			this.x = x;
			this.y = y;
		}
	}
}
