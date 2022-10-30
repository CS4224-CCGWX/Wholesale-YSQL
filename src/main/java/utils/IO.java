package utils;

import java.io.*;
import java.util.StringTokenizer;

public class IO extends PrintWriter {
    private BufferedReader r;
    private String line;

    private String filePath = "/home/stuproj/cs4224i/project_files/xact_files/%d.txt";
//    private String filePath = "/Users/siyuan/Desktop/NUS/cs4224/project/Wholesale-YSQL/project_files/xact_files/%d.txt";
    private StringTokenizer st;
    private String token;

    public IO(int client) throws FileNotFoundException {
        super(new BufferedOutputStream(System.out));
        File file = new File(String.format(filePath, client));
        r = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    }

    public boolean hasMoreTokens() {
        return peekToken() != null;
    }

    public String getLine() {
        return nextToken();
    }

    private String peekToken() {
        if (token == null) {
            try {
                while (st == null || !st.hasMoreTokens()) {
                    line = r.readLine();
                    if (line == null) return null;
                    st = new StringTokenizer(line);
                }
                token = st.nextToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return token;
    }

    private String nextToken() {
        String ans = peekToken();
        token = null;
        return ans;
    }
}


