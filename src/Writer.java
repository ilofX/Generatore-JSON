import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Writer implements Runnable {

    private File fOut;
    private PrintWriter write;
    private MainFrame mf;

    Writer(String path, MainFrame mf) throws IOException {
        this.fOut = new File(path);
        this.mf = mf;
        if (this.fOut.exists()) {
            this.fOut.delete();
        }
        this.fOut.createNewFile();
        this.write = new PrintWriter(fOut);
    }

    @Override
    public void run() {
        try {
            this.mf.samAquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.mf.logEvent("Started Writing JSON File");

        this.mf.logEvent("\nWriting JSON File");
        this.write.print(this.mf.getObject().toString());
        this.write.close();

        this.mf.samRelease();

        this.mf.logEvent("Conversion Terminated");
        this.mf.terminate();
    }

}
