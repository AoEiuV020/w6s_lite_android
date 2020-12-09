package com.foreveross.theme.model;

import com.google.gson.annotations.SerializedName;

/**
 * 创建时间：2016年09月07日 下午4:56
 * 创建人：laizihan
 * 类名：BaseColor
 * 用途：
 */
public class BaseColor {
    @SerializedName("c1")
    public String c1;
    @SerializedName("c2")
    public String c2;
    @SerializedName("c3")
    public String c3;
    @SerializedName("c4")
    public String c4;
    @SerializedName("c5")
    public String c5;
    @SerializedName("c6")
    public String c6;
    @SerializedName("c7")
    public String c7;
    @SerializedName("c8")
    public String c8;
    @SerializedName("c9")
    public String c9;
    @SerializedName("c10")
    public String c10;
    @SerializedName("c11")
    public String c11;
    @SerializedName("c12")
    public String c12;
    @SerializedName("c13")
    public String c13;
    @SerializedName("c14")
    public String c14;
    @SerializedName("c15")
    public String c15;
    @SerializedName("c16")
    public String c16;
    @SerializedName("c17")
    public String c17;
    @SerializedName("c18")
    public String c18;
    @SerializedName("c19")
    public String c19;
    @SerializedName("c20")
    public String c20;
    @SerializedName("c21")
    public String c21;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseColor baseColor = (BaseColor) o;

        if (c1 != null ? !c1.equals(baseColor.c1) : baseColor.c1 != null) return false;
        if (c2 != null ? !c2.equals(baseColor.c2) : baseColor.c2 != null) return false;
        if (c3 != null ? !c3.equals(baseColor.c3) : baseColor.c3 != null) return false;
        if (c4 != null ? !c4.equals(baseColor.c4) : baseColor.c4 != null) return false;
        if (c5 != null ? !c5.equals(baseColor.c5) : baseColor.c5 != null) return false;
        if (c6 != null ? !c6.equals(baseColor.c6) : baseColor.c6 != null) return false;
        if (c7 != null ? !c7.equals(baseColor.c7) : baseColor.c7 != null) return false;
        if (c8 != null ? !c8.equals(baseColor.c8) : baseColor.c8 != null) return false;
        if (c9 != null ? !c9.equals(baseColor.c9) : baseColor.c9 != null) return false;
        if (c10 != null ? !c10.equals(baseColor.c10) : baseColor.c10 != null) return false;
        if (c11 != null ? !c11.equals(baseColor.c11) : baseColor.c11 != null) return false;
        if (c12 != null ? !c12.equals(baseColor.c12) : baseColor.c12 != null) return false;
        if (c13 != null ? !c13.equals(baseColor.c13) : baseColor.c13 != null) return false;
        if (c14 != null ? !c14.equals(baseColor.c14) : baseColor.c14 != null) return false;
        if (c15 != null ? !c15.equals(baseColor.c15) : baseColor.c15 != null) return false;
        if (c16 != null ? !c16.equals(baseColor.c16) : baseColor.c16 != null) return false;
        if (c17 != null ? !c17.equals(baseColor.c17) : baseColor.c17 != null) return false;
        if (c18 != null ? !c18.equals(baseColor.c18) : baseColor.c18 != null) return false;
        if (c19 != null ? !c19.equals(baseColor.c19) : baseColor.c19 != null) return false;
        if (c20 != null ? !c20.equals(baseColor.c20) : baseColor.c20 != null) return false;
        return c21 != null ? c21.equals(baseColor.c21) : baseColor.c21 == null;

    }

    @Override
    public int hashCode() {
        int result = c1 != null ? c1.hashCode() : 0;
        result = 31 * result + (c2 != null ? c2.hashCode() : 0);
        result = 31 * result + (c3 != null ? c3.hashCode() : 0);
        result = 31 * result + (c4 != null ? c4.hashCode() : 0);
        result = 31 * result + (c5 != null ? c5.hashCode() : 0);
        result = 31 * result + (c6 != null ? c6.hashCode() : 0);
        result = 31 * result + (c7 != null ? c7.hashCode() : 0);
        result = 31 * result + (c8 != null ? c8.hashCode() : 0);
        result = 31 * result + (c9 != null ? c9.hashCode() : 0);
        result = 31 * result + (c10 != null ? c10.hashCode() : 0);
        result = 31 * result + (c11 != null ? c11.hashCode() : 0);
        result = 31 * result + (c12 != null ? c12.hashCode() : 0);
        result = 31 * result + (c13 != null ? c13.hashCode() : 0);
        result = 31 * result + (c14 != null ? c14.hashCode() : 0);
        result = 31 * result + (c15 != null ? c15.hashCode() : 0);
        result = 31 * result + (c16 != null ? c16.hashCode() : 0);
        result = 31 * result + (c17 != null ? c17.hashCode() : 0);
        result = 31 * result + (c18 != null ? c18.hashCode() : 0);
        result = 31 * result + (c19 != null ? c19.hashCode() : 0);
        result = 31 * result + (c20 != null ? c20.hashCode() : 0);
        result = 31 * result + (c21 != null ? c21.hashCode() : 0);
        return result;
    }
}
