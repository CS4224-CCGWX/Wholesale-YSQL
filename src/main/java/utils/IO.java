package utils;

import java.io.*;
import java.util.StringTokenizer;

//public class IO extends PrintWriter {
public class IO {
    private BufferedReader r;
    private String line;

    private String filePath;

    private StringTokenizer st;
    private String token;
    private int client;

    public IO(int client) {
//        super(new BufferedOutputStream(System.out));
//        super(System.out);
        this.client = client;
    }

    public void setFilePath(String path) throws FileNotFoundException {
        this.filePath = path;
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

    public void println(Object str) {
        System.out.println(str.toString());
    }

    public void close() {
        return;
    }
}


