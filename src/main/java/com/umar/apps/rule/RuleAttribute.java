package com.umar.apps.rule;

import com.umar.apps.rule.engine.WorkflowItem;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity(name = "RuleAttribute")
@Table(name = "attributes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"attribute_name", "rule_type"})
})
public class RuleAttribute implements WorkflowItem<Long>, Serializable {

    public RuleAttribute(){}

    private Long id;
    private String attributeName;
    private String ruleType;
    private int version;
    private String displayName;
    private BusinessRule businessRule;
    private List<RuleAttributeValue> ruleAttributeValues = new ArrayList<>();

    public RuleAttribute(Long id, String attributeName, String ruleType, String displayName) {
        this.id = id;
        this.attributeName = attributeName;
        this.ruleType = ruleType;
        this.displayName = displayName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @OneToMany(mappedBy = "ruleAttribute",cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<RuleAttributeValue> getRuleAttributeValues() {
        return ruleAttributeValues;
    }

    @ManyToOne
    @JoinTable(
            name = "rule_attribute",
            joinColumns = @JoinColumn(name = "attribute_id"),
            inverseJoinColumns = @JoinColumn(name = "rule_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"attribute_id","rule_id"})
    )
    public BusinessRule getBusinessRule() {
        return businessRule;
    }

    @Column(name = "attribute_name", length = 30)
    public String getAttributeName() {
        return attributeName;
    }

    @Column(name = "rule_type", length = 30)
    public String getRuleType() {
        return ruleType;
    }

    @Column(name = "version")
    public int getVersion() {
        return version;
    }

    @Column(name = "display_name", length = 60)
    public String getDisplayName() {
        return displayName;
    }

    public void setRuleAttributeValues(List<RuleAttributeValue> ruleAttributeValues) {
        this.ruleAttributeValues = ruleAttributeValues;
    }

    public void setBusinessRule(BusinessRule businessRule) {
        this.businessRule = businessRule;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RuleAttribute that)) return false;
        return attributeName.equals(that.attributeName) &&
                ruleType.equals(that.ruleType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeName, ruleType);
    }

    @Override
    public String toString() {
        return "RuleAttribute{" +
                "id=" + id +
                ", attributeName='" + attributeName + '\'' +
                ", ruleType='" + ruleType + '\'' +
                '}';
    }
}
