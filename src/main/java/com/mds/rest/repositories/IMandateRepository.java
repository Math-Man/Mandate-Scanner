package com.mds.rest.repositories;

import com.mds.rest.models.Mandate;
import com.mds.rest.models.MandateComparison;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMandateRepository// extends MongoRepository<Mandate, String>
{
    Mandate save(Mandate mandate);

    Mandate findMandateByName(String name);

    Mandate findMandateByNameAndVersion(String name, String version);

    Mandate getLatestMandateVersion(String documentName);

    List<Mandate> findMandatesByName(String name);




    MandateComparison saveMandateComparison(MandateComparison comparison);

    MandateComparison findMandateComparison(String id);

    MandateComparison findMandateComparison(String documentName, String from, String to);

    long count();

}
