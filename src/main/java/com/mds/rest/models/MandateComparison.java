package com.mds.rest.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MandateComparison
{
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String documentName;
    private String versionFrom;
    private String versionTo;
    private Date compareDate;
    private List<String> compareDiff;

    public List<String> getCompareDiff()
    {
        return compareDiff;
    }

    public void setCompareDiff(List<String> compareDiff)
    {
        this.compareDiff = compareDiff;
    }

    public ObjectId getId()
    {
        return id;
    }

    public void setId(ObjectId id)
    {
        this.id = id;
    }

    public String getDocumentName()
    {
        return documentName;
    }

    public void setDocumentName(String documentName)
    {
        this.documentName = documentName;
    }

    public String getVersionFrom()
    {
        return versionFrom;
    }

    public void setVersionFrom(String versionFrom)
    {
        this.versionFrom = versionFrom;
    }

    public String getVersionTo()
    {
        return versionTo;
    }

    public void setVersionTo(String versionTo)
    {
        this.versionTo = versionTo;
    }

    public Date getCompareDate()
    {
        return compareDate;
    }

    public void setCompareDate(Date compareDate)
    {
        this.compareDate = compareDate;
    }


    @Override
    public String toString()
    {
        return "MandateComparison{" +
                "id=" + id +
                ", documentName='" + documentName + '\'' +
                ", versionFrom='" + versionFrom + '\'' +
                ", versionTo='" + versionTo + '\'' +
                ", compareDate=" + compareDate +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MandateComparison that = (MandateComparison) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(documentName, that.documentName) &&
                Objects.equals(versionFrom, that.versionFrom) &&
                Objects.equals(versionTo, that.versionTo) &&
                Objects.equals(compareDate, that.compareDate);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, documentName, versionFrom, versionTo, compareDate);
    }
}
