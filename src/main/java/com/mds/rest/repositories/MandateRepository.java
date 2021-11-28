package com.mds.rest.repositories;

import com.mds.rest.models.Mandate;
import com.mds.rest.models.MandateComparison;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Repository("MandateRepository")
public class MandateRepository implements IMandateRepository
{
    @Value("${spring.data.mongodb.database}")
    private String database;
    @Value("${data.mongo.documents.mandates}")
    private String mandatesDoc;
    @Value("${data.mongo.documents.comparison}")
    private String mandateComprison;

    private static final TransactionOptions transactionOptions = TransactionOptions.builder()
            .readPreference(ReadPreference.primary())
            .readConcern(ReadConcern.MAJORITY)
            .writeConcern(WriteConcern.MAJORITY)
            .build();
    private final MongoClient client;
    private MongoCollection<Mandate> mandatesCollection;
    private MongoCollection<MandateComparison> comparisonCollection;

    public MandateRepository(MongoClient client)
    {
        this.client = client;
    }

    @PostConstruct
    void init()
    {
        mandatesCollection = client.getDatabase(database).getCollection(mandatesDoc, Mandate.class);
        comparisonCollection = client.getDatabase(database).getCollection(mandateComprison, MandateComparison.class);
    }

    @Override
    public Mandate save(Mandate mandate)
    {
        mandate.setId(new ObjectId());
        mandatesCollection.insertOne(mandate);
        return mandate;
    }

    @Override
    public Mandate findMandateByName(String name)
    {
        return mandatesCollection.find(eq("documentName", name)).first();
    }


    @Override
    public Mandate findMandateByNameAndVersion(String name, String version)
    {
        BasicDBObject query = new BasicDBObject();
        query.put("documentName", name);
        query.put("documentVersion", version);
        return mandatesCollection.find(query).first();
    }

    @Override
    public Mandate getLatestMandateVersion(String documentName)
    {
        //https://stackoverflow.com/a/69401903
        return mandatesCollection.find(eq("documentName", documentName))
                .sort(new BasicDBObject("documentVersion", -1))
                .limit(1)
                .first();
    }

    @Override
    public List<Mandate> findMandatesByName(String name)
    {
        return mandatesCollection.find(eq("documentName", name)).into(new ArrayList<>());
    }

    @Override
    public MandateComparison saveMandateComparison(MandateComparison comparison)
    {
        comparison.setId(new ObjectId());
        comparisonCollection.insertOne(comparison);
        return comparison;
    }

    @Override
    public MandateComparison findMandateComparison(String id)
    {
        return comparisonCollection.find(eq("_id", new ObjectId(id))).first();
    }

    @Override
    public MandateComparison findMandateComparison(String documentName, String from, String to)
    {
        BasicDBObject query = new BasicDBObject();
        query.put("documentName", documentName);
        query.put("versionFrom", from);
        query.put("versionTo", to);
        return comparisonCollection.find(query).first();
    }

    @Override
    public long count()
    {
        return mandatesCollection.countDocuments();
    }
}
