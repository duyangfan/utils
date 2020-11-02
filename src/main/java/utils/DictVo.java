package utils;

import java.io.Serializable;

public class DictVo implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = -648480347381032550L;
    private String value;
    private String text;
    private boolean selected;
    private String group;
    private Integer sort;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
