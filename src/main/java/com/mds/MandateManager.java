package com.mds;

import com.mds.rest.models.Mandate;
import com.mds.rest.models.MandateComparison;
import com.mds.rest.repositories.IMandateRepository;
import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


//https://www.baeldung.com/spring-scheduled-tasks

@Component
public class MandateManager //implements Runnable
{
    @Autowired
    @Qualifier("MandateRepository")
    private IMandateRepository repository;

    @Value("${url.gib.download}")
    private String ebynURL;


    private final File extractDirectory = new File("Extract");
    private final File ebynTarFile = new File("ebyn.tar.gz");
    private final Charset iso88591charset = Charset.forName("ISO-8859-1");

    @PostConstruct
    public void init()
    {
        //Enabled this once to build the DB
        //generateMandateDB(true);
    }


    private void downloadGibZipFile() throws IOException
    {
        FileUtils.delete(ebynTarFile);
        FileUtils.copyURLToFile(new URL(ebynURL), ebynTarFile);
    }

    private void unzipAndExtract() throws IOException
    {
        FileUtils.cleanDirectory(extractDirectory);
        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
        archiver.extract(ebynTarFile, extractDirectory);
    }

    private File findDocumentFile(String DocumentName, int DocumentVersion)
    {
        File root = extractDirectory;
        FilenameFilter nameFilter = new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.startsWith(DocumentName + "_" + DocumentVersion + "_") && name.endsWith(".xml");
            }
        };
        var files = Arrays.asList(root.listFiles(nameFilter));
        if(files.size() > 0)
            return files.get(0);

        return null;
    }

    /**
     * Generate database using extracted data from the gib site
     */
    private List<Mandate> generateMandateDB(boolean forceCreate)
    {
        File root = extractDirectory;
        FilenameFilter nameFilter = new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith("_Kodlar.xml");
            }
        };

        var files = Arrays.asList(root.listFiles(nameFilter)).stream().map(file ->
        {
            var fileNameProps = file.getName().split("_");
            var docName = fileNameProps[0];
            var docVersion = fileNameProps[1];
            return generateMandateRecord(docName, docVersion);

        }).collect(Collectors.toList());

        if(!forceCreate)
            files = files.stream()
                .filter(mandate -> repository.findMandateByNameAndVersion(mandate.getDocumentName(), mandate.getDocumentVersion()) != null)
                .collect(Collectors.toList());
        return files;
    }


    private Mandate generateMandateRecord(String document, String version)
    {
        Mandate mandate = new Mandate();
        mandate.setDocumentName(document);
        mandate.setDocumentVersion(version);
        mandate.setDocumentLastUpdate(new Date());
        repository.save(mandate);
        return mandate;
    }

    @Scheduled(fixedRate = 8640000, initialDelay = 2000000000)
    public void scheduleFixedRateTaskAsync() throws InterruptedException
    {
        System.out.println("Fixed rate task async - " + System.currentTimeMillis() / 1000 + " " + repository);
        try
        {
            downloadGibZipFile();
            unzipAndExtract();
            generateMandateDB(repository.count() == 0);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("Download/Unpack done");
        Thread.sleep(2000);
    }


    public MandateComparison GenerateComparison(String documentName, String versionFrom, String versionTo)
    {
        MandateComparison comparison = new MandateComparison();
        comparison.setId(new ObjectId());
        comparison.setCompareDate(Date.from(Instant.now()));
        comparison.setDocumentName(documentName);
        comparison.setVersionFrom(versionFrom);
        comparison.setVersionTo(versionTo);

        int versionToInt = Integer.parseInt(versionTo);
        int versionFromInt = Integer.parseInt(versionFrom);

        //Generate changelog
        comparison.setCompareDiff(compareXMLLineByLineAsCleanString(findDocumentFile(documentName, versionFromInt), findDocumentFile(documentName, versionToInt)));

        return comparison;
    }

    private List<MandateLineChange> compareXMLLineByLine(File file1, File file2)
    {
        try
        {
            List<String> list1 = Files.lines(Paths.get(file1.getPath()), iso88591charset).collect(Collectors.toList());
            List<String> list2 = Files.lines(Paths.get(file2.getPath()), iso88591charset).collect(Collectors.toList());

            List<MandateLineChange> file2Changes = IntStream.range(0, list2.size())
                    .mapToObj(i -> new MandateLineChange(i, list2.get(i)))
                    .filter(mlc -> !list1.contains(mlc.line))
                    .peek(mlc -> {System.out.println("Mismatch @" + mlc.lineNr + " " + mlc.line);})
                    .collect(Collectors.toList());

            return file2Changes;
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
        return null;
    }

    private List<String> compareXMLLineByLineAsCleanString(File file1, File file2)
    {
        Locale trlocale = new Locale("tr-TR");

        return compareXMLLineByLine(file1, file2).stream().map(fl ->
        {
            byte[] convBytes = fl.toString().replace("\t", "").getBytes();
            return clearTurkishChars((new String(convBytes, StandardCharsets.UTF_8)).toLowerCase(trlocale));
        }
        ).collect(Collectors.toList());
    }

    private String clearTurkishChars(String str) {
        String ret = str;
        char[] turkishChars = new char[] {0x131, 0x130, 0xFC, 0xDC, 0xF6, 0xD6, 0x15F, 0x15E, 0xE7, 0xC7, 0x11F, 0x11E};
        char[] englishChars = new char[] {'i', 'I', 'u', 'U', 'o', 'O', 's', 'S', 'c', 'C', 'g', 'G'};
        for (int i = 0; i < turkishChars.length; i++) {
            ret = ret.replaceAll(new String(new char[]{turkishChars[i]}), new String(new char[]{englishChars[i]}));
        }
        return ret;
    }


    class MandateLineChange
    {
        public int lineNr;
        public String line;

        public MandateLineChange(int lineNr, String line)
        {
            this.line = line;
            this.lineNr = lineNr;
        }

        @Override
        public String toString()
        {
            return "{" +
                    "lineNr=" + lineNr +
                    ", line='" + line + '\'' +
                    '}';
        }
    }

    /*
    @Bean
    @Override
    public void run()
    {
        System.out.println("what"+repository);
//        repository.save(new Mandate().setDocumentName("SGK1").setDocumentVersion("1").setDocumentLastUpdate(Date.valueOf(LocalDate.now())).setId(new ObjectId()));
//
//        System.out.println("here: " + repository.findMandateByName("SGK1"));
    }
    */
}

