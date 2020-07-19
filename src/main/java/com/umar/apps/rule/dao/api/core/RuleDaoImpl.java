package com.umar.apps.rule.dao.api.core;

import com.umar.apps.rule.BusinessRule;
import com.umar.apps.rule.dao.api.RuleDao;
import com.umar.apps.rule.infra.dao.api.core.GenericJpaDao;
import com.umar.simply.jdbc.dml.operations.SelectOp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import javax.persistence.NoResultException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.umar.apps.rule.BusinessRule.*;
import static com.umar.apps.rule.RuleValue.*;

public class RuleDaoImpl extends GenericJpaDao<BusinessRule, Long> implements RuleDao {

    private static final Logger logger = LogManager.getLogger(RuleDaoImpl.class);

    public RuleDaoImpl(final String persistenceUnit) {
        super(BusinessRule.class, persistenceUnit);
    }

    @Override
    public Collection<BusinessRule> findAll() {
        logger.info("findAll()");
        Collection<BusinessRule> rules = new ArrayList<>(Collections.emptyList());
        String sql = SelectOp.create()
                .SELECT().COLUMN(RULE$RULE)
                .FROM(RULE$ALIAS)
                .getSQL();
        executeInTransaction(entityManager -> {
            List<?> result = entityManager.createQuery(sql)
                    .getResultList();
            result.forEach(row -> {
                rules.add((BusinessRule) row);
            });
        });
        return rules;
    }

    @Override
    public Collection<BusinessRule> findByName(String ruleName) {
        logger.info("findByName() with name: {}", ruleName);
        Collection<BusinessRule> businessRules = new ArrayList<>(Collections.emptyList());
        String sql = SelectOp.create()
                .SELECT().COLUMN(RULE$RULE)
                .FROM(RULE$ALIAS)
                .WHERE().COLUMN(RULE$RULE_NAME).EQ(":ruleName")
                .AND().COLUMN(RULE$ACTIVE).EQ(":active")
                .getSQL();
        executeInTransaction(entityManager -> {
            List<?> result = entityManager.createQuery(sql)
                    .setParameter("ruleName", ruleName)
                    .setParameter("active", true)
                    .getResultList();
            result.forEach(row -> {
                businessRules.add((BusinessRule) row);
            });
        });
        return businessRules;
    }

    @Override
    public Collection<BusinessRule> findByType(String type) {
        logger.info("findByType() with type: {}", type);
        Collection<BusinessRule> businessRules = new ArrayList<>(Collections.emptyList());
        String sql = SelectOp.create()
                .SELECT().COLUMN(RULE$RULE)
                .FROM(RULE$ALIAS)
                .WHERE().COLUMN(RULE$RULE_TYPE).EQ(":type")
                .AND().COLUMN(RULE$ACTIVE).EQ(":active")
                .getSQL();
        executeInTransaction(entityManager -> {
            List<?> result = entityManager.createQuery(sql)
                    .setParameter("type", type)
                    .setParameter("active", true)
                    .getResultList();
            result.forEach(row -> {
                businessRules.add((BusinessRule) row);
            });
        });
        return businessRules;
    }

    @Override
    public Collection<BusinessRule> findActiveRules(boolean isActive) {
        logger.info("findActiveRules() with isActive: {}", isActive);
        Collection<BusinessRule> businessRules = new ArrayList<>(Collections.emptyList());
        String sql = SelectOp.create()
                .SELECT().COLUMN(RULE$RULE)
                .FROM(RULE$ALIAS)
                .WHERE().COLUMN(RULE$ACTIVE).EQ(":active")
                .getSQL();
        executeInTransaction(entityManager -> {
            List<?> result = entityManager.createQuery(sql)
                    .setParameter("active", isActive)
                    .getResultList();
            result.forEach(row -> {
                businessRules.add((BusinessRule) row);
            });
        });
        return businessRules;
    }

    @Override
    public Optional<BusinessRule> findByNameAndType(String ruleName, String ruleType) {
        logger.info("findByNameAndType() with ruleName: {}, ruleType: {}", ruleName, ruleType);
        AtomicReference<Object> result = new AtomicReference<>();
        String sql = SelectOp.create()
                .SELECT()
                .COLUMN(RULE$RULE)
                .FROM(RULE$ALIAS)
                .WHERE()
                .COLUMN(RULE$RULE_NAME)
                .EQ(":ruleName")
                .AND()
                .COLUMN(RULE$RULE_TYPE)
                .EQ(":ruleType")
                .getSQL();
        executeInTransaction(entityManager -> {
            try {
                result.set(entityManager.createQuery(sql)
                        .setParameter("ruleName", ruleName)
                        .setParameter("ruleType", ruleType)
                        .getSingleResult());
            }catch (NoResultException e) {
                //Simply ignore it. This is expected when no data exist.
            }
        });
        if(null != result.get()) {
            BusinessRule rule = (BusinessRule) result.get();
            return Optional.of(rule);
        }
        return Optional.empty();
    }

    @Override
    public Optional<BusinessRule> findByNameTypeAndOperand(String ruleName, String ruleType, String operand) {
        logger.info("findByNameTypeAndOperand() with ruleName: {}, ruleType: {}, operand: {}", ruleName, ruleType, operand);
        AtomicReference<Object> result = new AtomicReference<>();
        String sql = SelectOp.create()
                .SELECT().COLUMN(RULE$RULE).FROM(RULE$ALIAS)
                .JOIN().TABLE(RULE_VALUE$ALIAS)
                .ON().COLUMN(RULE$RULE).EQ(RULE_VALUE$RULE)
                .WHERE().COLUMN(RULE$RULE_NAME).EQ(":ruleName")
                .AND().COLUMN(RULE$RULE_TYPE).EQ(":ruleType")
                .AND().COLUMN(RULE_VALUE$OPERAND).EQ(":operand")
                .getSQL();
        executeInTransaction(entityManager -> {
            try {
                Object row = entityManager.createQuery(sql)
                        .setParameter("ruleName", ruleName)
                        .setParameter("ruleType", ruleType)
                        .setParameter("operand", operand).getSingleResult();
                result.set(row);
            }catch (NoResultException e) {
                //Simply ignore it. This is expected when no data exist.
            }
        });
        if(null != result.get()){
            BusinessRule businessRule = (BusinessRule) result.get();
            return Optional.of(businessRule);
        }
        return Optional.empty();
    }

    @Override
    public Optional<BusinessRule> findByNameTypeAndOperands(String ruleName, String ruleType, List<String> operands) {
        logger.info("findByNameTypeAndOperand() with ruleName: {}, ruleType: {}, operand: {}", ruleName, ruleType, operands);
        AtomicReference<BusinessRule> result = new AtomicReference<>();
        String sql = SelectOp.create()
                .SELECT().COLUMN(RULE$RULE).FROM(RULE$ALIAS)
                .JOIN().TABLE(RULE_VALUE$ALIAS)
                .ON().COLUMN(RULE$RULE).EQ(RULE_VALUE$RULE)
                .WHERE().COLUMN(RULE$RULE_NAME).EQ(":ruleName")
                .AND().COLUMN(RULE$RULE_TYPE).EQ(":ruleType")
                .AND().COLUMN(RULE_VALUE$OPERAND).COLUMN(" IN : operands")
                .getSQL();
        executeInTransaction(entityManager -> {
            Session session = entityManager.unwrap(Session.class);
            try {
                BusinessRule row = session.createQuery(sql, BusinessRule.class)
                        .setParameter("ruleName", ruleName)
                        .setParameter("ruleType", ruleType)
                        .setParameterList("operands", operands).getSingleResult();
                result.set(row);
            }catch (Exception e) {
                //Simply ignore it. This is expected when no data exist.
                e.printStackTrace();
            }
        });
        if(null != result.get()){
            BusinessRule businessRule = (BusinessRule) result.get();
            return Optional.of(businessRule);
        }
        return Optional.empty();
    }
}