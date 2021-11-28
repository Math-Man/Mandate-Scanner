package com.mds.rest.controllers;

import com.mds.MandateManager;
import com.mds.rest.models.Mandate;
import com.mds.rest.models.MandateComparison;
import com.mds.rest.repositories.IMandateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class MandateController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(MandateController.class);

    private final IMandateRepository mandateRepository;

    public MandateController(IMandateRepository mandateRepository)
    {
        this.mandateRepository = mandateRepository;
    }

    private final MandateManager mandateManager = new MandateManager();

    /*
    @PostMapping("mandate")
    @ResponseStatus(HttpStatus.CREATED)
    public Mandate postMandate(@RequestBody Mandate mandate)
    {
        return mandateRepository.save(mandate);
    }
    */

    /*
    @GetMapping("mandate/{name}")
    public ResponseEntity<Mandate> getMandate(@PathVariable String name)
    {
        Mandate mandate = mandateRepository.findMandateByName(name);
        if (mandate == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(mandate);
    }
    */

    @GetMapping("mandate/latest/{name}")
    public ResponseEntity<Mandate> getLatestMandate(@PathVariable String name)
    {
        Mandate mandate = mandateRepository.getLatestMandateVersion(name);
        if (mandate == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(mandate);
    }

    @GetMapping("mandate/count")
    public Long getCount()
    {
        return mandateRepository.count();
    }

    @GetMapping("mandate/all/{DocumentName}")
    public ResponseEntity<List<Mandate>> getAllDocuments(@PathVariable String DocumentName)
    {
        var result = mandateRepository.findMandatesByName(DocumentName);
        if (result == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(result); //(ResponseEntity<List<Mandate>>) mandateRepository.findMandatesByName(DocumentName);
    }


    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final Exception handleAllExceptions(RuntimeException e)
    {
        LOGGER.error("Internal server error.", e);
        return e;
    }


    @GetMapping("compare/{id}")
    public ResponseEntity<MandateComparison> getComparison(@PathVariable String id)
    {
        MandateComparison comparison = mandateRepository.findMandateComparison(id);
        if (comparison == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(comparison);
    }

    @GetMapping("compare/{DocumentName}/{VersionFrom}/{VersionTo}")
    public ResponseEntity<MandateComparison> getComparison(@PathVariable String DocumentName, @PathVariable String VersionFrom, @PathVariable String VersionTo)
    {
        MandateComparison comparison = mandateRepository.findMandateComparison(DocumentName, VersionFrom, VersionTo);

        //TODO: Refactor to another method
        if (comparison == null)
        {
            System.out.println("uh oh I dont have that comp...");
            var foundMandates = mandateRepository.findMandatesByName(DocumentName);
            if(foundMandates != null && foundMandates.size() > 0)
            {
                boolean fromFound = false;
                boolean toFound = false;

                for(Mandate m : foundMandates)
                {
                    if(m.getDocumentVersion().equals(VersionFrom))
                        fromFound = true;
                    else if(m.getDocumentVersion().equals(VersionTo))
                        toFound = true;

                    if(fromFound && toFound)
                        break;
                }
                if(!(fromFound && toFound))
                {
                    System.out.println("No such mandate(s) exists!!");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            }
            else
            {
                System.out.println("No such mandate exists");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            MandateComparison comp = mandateRepository.saveMandateComparison(mandateManager.GenerateComparison(DocumentName,VersionFrom,VersionTo));
            return ResponseEntity.ok(comp);
        }
        else
        {
            System.out.println("Already Exists");
            return ResponseEntity.ok(comparison);
        }
    }

    /*
    @PostMapping("compare")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MandateComparison> postComprasion(@RequestBody MandateComparison comp)
    {
        try
        {
            if(Integer.parseInt(comp.getVersionFrom()) > Integer.parseInt(comp.getVersionTo()))
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        catch (NumberFormatException e)
        {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mandateRepository.saveMandateComparison(comp));
    }
    */



}
