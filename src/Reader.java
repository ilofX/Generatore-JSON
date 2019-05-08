import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Reader implements Runnable {

    private File fIn;
    private Scanner read;
    private JSONObject object;
    private MainFrame mf;

    Reader(String path, MainFrame mf) throws FileNotFoundException {
        this.fIn = new File(path);
        this.mf = mf;
        if (!fIn.exists())
            throw new FileNotFoundException();
        this.read = new Scanner(fIn);
        this.object = new JSONObject();
    }

    @Override
    public void run() {
        String line, provincia = "";
        String[] splitString;
        JSONArray array = null;
        JSONObject object;


        try {
            mf.samAquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.mf.logEvent("Started Parsing...");

        while (read.hasNextLine()) {
            line = read.nextLine();
            if (line.isEmpty() || line.charAt(0) == '#')
                continue;
            else if (line.charAt(0) == '!') {
                if (array != null && !provincia.equals(""))
                    this.object.put(provincia, array);
                this.mf.logEvent("Terminated Parsing " + provincia);
                provincia = line.substring(1);
                array = new JSONArray();
                this.mf.logEvent("\nStarted Parsing " + provincia);
            } else {       //Nome,Via,CAP,Comune,Licenza,Privato,info
                object = new JSONObject();
                splitString = line.split(",");
                object.put("Nome", splitString[0]);
                object.put("Via", splitString[1]);
                //System.out.println(splitString[2]);
                object.put("CAP", Integer.parseInt(splitString[2]));
                object.put("Comune", splitString[3]);
                object.put("Licenza", (splitString[4].equals("true")));
                object.put("Privato", (splitString[5].equals("true")));
                if (splitString.length > 6)
                    object.put("info", splitString[6]);
                array.put(object);
                this.mf.logEvent("Parsed object " + splitString[0]);
            }
        }
        this.mf.setObject(this.object);
        this.read.close();
        mf.samRelease();

        this.mf.logEvent("Parsing Terminated");
    }

}
