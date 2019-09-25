package com.example.raymond.student.Model;

public class RulesModel {
    private String title, rule;
    private long ruleDate;


    public RulesModel() {
    }

    public RulesModel(String title, String rule, long ruleDate) {
        this.title = title;
        this.rule = rule;
        this.ruleDate = ruleDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public long getRuleDate() {
        return ruleDate;
    }

    public void setRuleDate(long ruleDate) {
        this.ruleDate = ruleDate;
    }
}
