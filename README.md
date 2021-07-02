## Image Generator

Tasked to create Structured image, Multithread support, Serial support, CSV with serial, Barcode support, scalable (x,y)
coordinate support, uses font in system, only need to get the barcode library else is java library.

##Required Info
`image`, `imgINFO.txt`

###Would Generate
`folder`, `image`, `CSV.csv`

####Note: if not provided with command line argument of `image Name` And `infoInfo` it would look for `img.xxx` `imgInfo.txt` in current directory

**Structure:**

    info
    info
    info
    [new line]
    Team-
        |-Name
        |-Name
        |-Name
    Team-
        |-Name
        |-Name

**Functional Supports:**

`Team:` The folder where Image would be put into. It can effect The Image if needed

    Can create Folder
    Can't create File
    Can effect on Image
    
    Note: Alias command that you would provide via .txt -t Team

`Name:` It's responsible to create the image files according to the name

    Can't create Folder
    Can create File
    Can effect on Image
    Note: Alias command that you would provide via .txt -n Name

`Quotes:` It can currently manipulate the Image without and effect on file or folder structure

    Can't create Folder
    Can't create File
    Can effect on Image
    Note: Alias command that you would provide via .txt -q Quotes

**Styling Supports:**

    x cordinate x[number]. Where you want to effect the image
        Can be fixed cordinates example: x540
        Can be a ratio with your image (540/1080) exaple: x.5 (Recomended as the position stays the same with change in picture witdth)
    
    y cordinate y[number]. Where you want to effect the image
        Can be fixed cordinates example: y640
        Can be a ratio with your image (640/1920) exaple: y.33333333 (Recomended as the position stays the same with change in picture height)
    
    h height h[number]. How big you want your effect to be
        Can be fixed Height example: h66
        Can be a ratio with your image (66/1080) exaple: x.06111111 (Recomended as the position stays the same with change in picture witdth)
    
    w width w[number]. How big you want your effect to be
        Can be fixed width example: w66
        Can be a ratio with your image (66/1920) exaple: x.034375 (Recomended as the position stays the same with change in picture height)
    
    s size s[number]. How big the text would be
        Can be fixed example: s72
        Can be a ratio with your image (72/1920) exaple: x.0375 (Recomended as the position stays the same with change in picture height)
    
    f font name f[name]. Which font you want to use.
        You can use every single font in your system example:Economica
        For text that have space in there name replace " " with "_". Palace Script MT would look like example: Palace-Script-MT
    
    # color #[hex code of color]. Which color you want to use in your text
        You can use any color with its hex code example: #353535

    t text serial maker[text]. If you want to create serial Number in your image
        used with -s
        Its Smart text where you would have to point out where the serial would increment example: NSUACSC-20302##
            the "#" sign are where the number would increment. you can use as many as 9 "#" sign and it would keep incrementing for each image like this:
            NSUACSC-20302## becomes:
            NSUACSC-2030201
            NSUACSC-2030202
            ...


**Features**

`Serial:` Relax as it would take care of it itself

`Barcode:` Relax as it would take care of itself


