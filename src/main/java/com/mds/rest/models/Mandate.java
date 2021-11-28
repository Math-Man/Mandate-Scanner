package com.mds.rest.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mandate
{

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String documentName;
    private String documentVersion;
    private Date documentLastUpdate;

    public ObjectId getId()
    {
        return id;
    }

    public Mandate setId(ObjectId id)
    {
        this.id = id;
        return this;
    }

    public String getDocumentName()
    {
        return documentName;
    }

    public Mandate setDocumentName(String documentName)
    {
        this.documentName = documentName;
        return this;
    }

    public Date getDocumentLastUpdate()
    {
        return documentLastUpdate;
    }

    public Mandate setDocumentLastUpdate(Date documentLastUpdate)
    {
        this.documentLastUpdate = documentLastUpdate;
        return this;
    }

    public String getDocumentVersion()
    {
        return documentVersion;
    }

    public Mandate setDocumentVersion(String documentVersion)
    {
        this.documentVersion = documentVersion;
        return this;
    }

    @Override
    public String toString()
    {
        return "Mandate{" +
                "id=" + id +
                ", documentName='" + documentName + '\'' +
                ", documentVersion='" + documentVersion + '\'' +
                ", documentLastUpdate=" + documentLastUpdate +
                '}';
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mandate mandate = (Mandate) o;
        return Objects.equals(id, mandate.id) &&
                Objects.equals(documentName, mandate.documentName) &&
                Objects.equals(documentVersion, mandate.documentVersion) &&
                Objects.equals(documentLastUpdate, mandate.documentLastUpdate);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, documentName, documentVersion, documentLastUpdate);
    }
}
