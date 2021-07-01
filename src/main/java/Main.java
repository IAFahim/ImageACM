import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.DecimalFormat;
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
    public String text = null;
}

class Data {
    public String team, quote, name;

    public Data() {

    }

    public Data(String team, String quote, String name) {
        this.name = name;
        this.quote = quote;
        this.team = team;
    }
}

public class Main {
    private static int totalCreated = 1;
    private static int totalInClass = 0;
    private static String imgPath, infoPath, imageType;
    private static Info serialInfo = new Info(), barcodeInfo = new Info(), teamInfo = new Info(), quoteInfo = new Info(), nameInfo = new Info();
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
        imageType = imgPath.substring(imgPath.lastIndexOf('.') + 1);
        build();
        System.out.println(System.currentTimeMillis() - x);
    }

    public static void build() {
        try {
            Scanner sc = new Scanner(new FileReader(infoPath));
            imageMain = ImageIO.read(new File(imgPath));
            qrCodeWriter = new QRCodeWriter();

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
            createImage();
        } catch (IOException e) {
            e.printStackTrace();
            exit("SomeThingWent Really wrong");
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
                        case 't' -> info.text = arr[i].substring(1).replaceAll("_", " ");
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
            case 'b' -> barcodeInfo = info;
            case 's' -> {
                serialInfo = info;
                canHoldInSerial = at(serialInfo.text);
            }
        }
    }

    private static int doubleCast(String str) {
        if (!Character.isLetter(str.charAt(0))) {
            exit(str);
        }
        double x = Double.parseDouble(str.substring(1));
        if ((int) x >= 1 || x == (int) x) {
            return (int) x;
        } else {
            if (str.charAt(0) == 'x') {
                return (int) (x * imageMain.getWidth());
            } else if (str.charAt(0) == 'y') {
                return (int) (x * imageMain.getHeight());
            } else {
                exit(str);
            }
        }
        return 0;
    }

    public static int canHoldInSerial;

    private static int at(String str) {
        int at = str.length() - 1;
        for (int i = at; str.charAt(i) == '#'; i--) {
            at = i;
        }
        return at;
    }

    public static String serial(int n) {
        String str = serialInfo.text;
        int gap = str.length() - canHoldInSerial;
        int len = (n + "").length();
        if (len < gap) {
            return str.substring(0, canHoldInSerial) + "0".repeat(len) + n;
        } else {
            return str.substring(0, canHoldInSerial) + n;
        }

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
                }
                case 'q' -> {
                    subStr = str.substring(3);
                    currentData.quote = subStr;
                }
            }
        } else {
            System.out.println(++totalInClass + ", " + str);
            datas.add(new Data(currentData.team, currentData.quote, str));
        }
    }

    static BufferedImage imageMain;
    static QRCodeWriter qrCodeWriter;

    public static void createImage() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < datas.size(); i++) {
            int finalI = i;
            executorService.submit(() -> {
                Data data = datas.get(finalI);
                BufferedImage image = copyImage(imageMain);
                if (data.team != null) {
                    Graphics2D g = (Graphics2D) image.getGraphics();
                    g.setFont(new Font(teamInfo.font, Font.PLAIN, teamInfo.size));
                    g.setColor(teamInfo.color);
                    String text = data.team;
                    TextLayout textLayout = new TextLayout(text, g.getFont(), g.getFontRenderContext());
                    double textHeight = textLayout.getBounds().getHeight(), textWidth = textLayout.getBounds().getWidth();
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    g.drawString(text, teamInfo.x - (int) textWidth / 2, teamInfo.y + (int) textHeight / 2);
                    g.dispose();
                }

                if (data.quote != null) {
                    Graphics2D g = (Graphics2D) image.getGraphics();
                    g.setFont(new Font(quoteInfo.font, Font.PLAIN, quoteInfo.size));
                    g.setColor(quoteInfo.color);
                    String text = data.quote;
                    TextLayout textLayout = new TextLayout(text, g.getFont(), g.getFontRenderContext());
                    double textHeight = textLayout.getBounds().getHeight(), textWidth = textLayout.getBounds().getWidth();
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    g.drawString(text, quoteInfo.x - (int) textWidth / 2, quoteInfo.y + (int) textHeight / 2);
                    g.dispose();
                }

                if (data.name != null) {
                    Graphics2D g = (Graphics2D) image.getGraphics();
                    g.setFont(new Font(nameInfo.font, Font.PLAIN, nameInfo.size));
                    g.setColor(nameInfo.color);
                    String text = data.name;
                    TextLayout textLayout = new TextLayout(text, g.getFont(), g.getFontRenderContext());
                    double textHeight = textLayout.getBounds().getHeight(), textWidth = textLayout.getBounds().getWidth();
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    g.drawString(text, nameInfo.x - (int) textWidth / 2, nameInfo.y + (int) textHeight / 2);
                    g.dispose();
                }

                if (serialInfo.text != null) {
                    Graphics2D g = (Graphics2D) image.getGraphics();
                    g.setFont(new Font(serialInfo.font, Font.PLAIN, serialInfo.size));
                    g.setColor(serialInfo.color);
                    String text = serial(totalCreated);
                    TextLayout textLayout = new TextLayout(text, g.getFont(), g.getFontRenderContext());
                    double textHeight = textLayout.getBounds().getHeight(), textWidth = textLayout.getBounds().getWidth();
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    g.drawString(text, serialInfo.x - (int) textWidth / 2, serialInfo.y + (int) textHeight / 2);

                    g.dispose();
                }

                if (barcodeInfo.text != null) {
                    StringBuilder sb = new StringBuilder(barcodeInfo.text);
                    if (data.name != null) {
                        sb.append(","+data.name);
                    }
                    if (data.team != null) {
                        sb.append(","+data.team);
                    }
                    if (data.quote != null) {
                        sb.append(","+data.quote);
                    }
                    if (barcodeInfo.x == 0 || barcodeInfo.y == 0 || barcodeInfo.height == 0 || barcodeInfo.width == 0) {
                        exit("Barcode Problem");
                    }

                    BufferedImage bufferedImage = qrCodeImage(sb.toString(), barcodeInfo.width, barcodeInfo.height);
                    Graphics2D g = (Graphics2D) image.getGraphics();
                    g.drawImage(bufferedImage, barcodeInfo.x-(barcodeInfo.width/2), barcodeInfo.y-(barcodeInfo.height/2), null);
                    g.dispose();
                }

                new File(data.team).mkdirs();
                try {
                    ImageIO.write(image, imageType, new File(data.team + "\\" + data.name + "." + imageType));
                    totalCreated++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();
    }

    public static BufferedImage qrCodeImage(String str, int width, int height) {

        BitMatrix bitMatrix = null;
        try {
            bitMatrix = qrCodeWriter.encode(str, BarcodeFormat.QR_CODE, width, height);
        } catch (WriterException e) {
            e.printStackTrace();
            exit(str);
        }
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage bi = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        byte[] sourceData = ((DataBufferByte) source.getRaster().getDataBuffer()).getData();
        byte[] biData = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourceData, 0, biData, 0, sourceData.length);
        return bi;
    }
}
