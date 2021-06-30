import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.image.*;
import java.util.concurrent.*;
import javax.imageio.ImageIO;

class Info {
    public int x = 0, y = 0, width = 0, height = 0, size = 0;
    public Color color = Color.black;
    public String font = "Arial";
}

class Data {
    public String team, quote, name;
}

public class Main {
    private static int totalCreated = 0;
    private static int totalInClass = 0;
    private static String imgPath, infoPath, currentDirectory;
    private static Info teamInfo = new Info(), quoteInfo = new Info(), nameInfo = new Info(), imageInfo = new Info();
    private static ArrayList<Data> datas = new ArrayList<>();

    public static void main(String[] args) {
        long x = System.currentTimeMillis();
        if (args.length == 2) {
            imgPath = args[0];
            infoPath = args[1];
        } else {
            imgPath = "img.png";
            infoPath = "imgINFO.txt";
        }
        build();
        System.out.println(System.currentTimeMillis() - x);
    }

    public static void build() {
        try {
            Scanner sc = new Scanner(new FileReader(infoPath));
            boolean hasInformation = false;
            while (sc.hasNextLine()) {
                String str = sc.nextLine();
                if (str.length() == 0) {
                    hasInformation = true;
                    continue;
                }
                if (hasInformation) {
                    dataFinder(str);
                } else {
                    infoFinder(str);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void infoFinder(String str) {
        char c = 'n';
        Info info = new Info();
        if (str.length() > 2 && str.charAt(0) == '-') {
            String[] arr = str.split(" ");
            for (int i = 1; i < arr.length; i++) {
                if (arr[i].length() > 0)
                    switch (arr[i].charAt(0)) {
                        case 'x' -> info.x = doubleCast(arr[i]);
                        case 'y' -> info.y = doubleCast(arr[i]);
                        case 'w' -> info.width = Integer.parseInt(arr[i].substring(1));
                        case 'h' -> info.height = Integer.parseInt(arr[i].substring(1));
                        case 's' -> info.size = Integer.parseInt(arr[i].substring(1));
                        case 'f' -> info.font = arr[i].substring(1).replaceAll("_", " ");
                        case '#' -> info.color = Color.decode(arr[i]);
                        default -> System.err.println("Error at:" + arr[i]);
                    }
            }

        }
        c = Character.toLowerCase(str.charAt(1));
        switch (c) {
            case 't' -> teamInfo = info;
            case 'q' -> quoteInfo = info;
            case 'n' -> nameInfo = info;
            case 'm' -> imageInfo = info;
        }
    }

    private static int doubleCast(String str) {
        if (!Character.isLetter(str.charAt(0))) {
            exit(str);
        }
        double x = Double.parseDouble(str.substring(1));

        if (imageInfo.x == 0 || imageInfo.y == 0) {
            if (x == (int) x) {
                return (int) x;
            } else {
                exit(str);
            }
        } else {
            if ((int)x >= 1 || x == (int) x) {
                return (int) x;
            } else {
                if (str.charAt(0) == 'x') {
                    return (int) (x * imageInfo.x);
                } else if (str.charAt(0) == 'y') {
                    return (int) (x * imageInfo.y);
                } else {
                    exit(str);
                }
            }
        }
        return 0;


    }

    static void exit(String str) {
        System.err.println("Error at " + str);
        System.exit(-1);
    }

    static Data currentData = new Data();

    public static void dataFinder(String str) {
        char c = 'n';
        if (str.length() > 2 && str.charAt(0) == '-') {
            c = Character.toLowerCase(str.charAt(1));
            String subStr;
            switch (c) {
                case 't' -> {
                    subStr = str.substring(3);
                    currentData.team = subStr;
                    System.out.println(subStr);
//                    makeDirectory(subStr);
                }
                case 'q' -> {
                    subStr = str.substring(3);
                    currentData.quote = subStr;
                }
            }
        } else {
            System.out.println(++totalInClass + ", " + str);
            currentData.name = str;
            totalCreated++;
            datas.add(currentData);
//            createFile(str);
        }
    }

    static BufferedImage imageMain;

    public static void createImage() {
        try {
            imageMain = ImageIO.read(new File(imgPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (Data data : datas) {
            executorService.submit(() -> {
                BufferedImage image = copyImage(imageMain);
                if (data.team != null) {
                    Graphics2D g = (Graphics2D) image.getGraphics();
                    Font font = new Font(teamInfo.font, Font.PLAIN, teamInfo.size);
                    g.setFont(font);
                    g.setColor(Color.BLACK);
                    String text = data.team;
                    TextLayout textLayout = new TextLayout(text, font, g.getFontRenderContext());
                    double textHeight = textLayout.getBounds().getHeight(), textWidth = textLayout.getBounds().getWidth();
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    g.drawString(text, image.getWidth() / 2 - (int) textWidth / 2, teamInfo.y);
                    g.dispose();
                }

                if (data.quote != null) {

                }

                if (data.name != null) {

                }

                try {
                    ImageIO.write(image, "png", new File(data.team + "/" + data.name));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        //cleaning
        totalInClass = 0;
        datas.clear();
    }

    public static void makeDirectory(String str) {
        new File(str).mkdirs();
        currentDirectory = str;
    }


    public static void createFile(String str) {
        try {
            new File(currentDirectory + "/" + str).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage bi = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        byte[] sourceData = ((DataBufferByte) source.getRaster().getDataBuffer()).getData();
        byte[] biData = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourceData, 0, biData, 0, sourceData.length);
        return bi;
    }
}
