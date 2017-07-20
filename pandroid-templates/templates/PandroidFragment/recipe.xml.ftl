<?xml version="1.0"?>
<recipe>

    <instantiate from="root/src/app_package/BasePandroidFragment.java.ftl"
                 to="${escapeXmlAttribute(srcOut)}/${fragmentClass}.java"/>

    <instantiate from="root/src/app_package/BasePandroidFragmentOpener.java.ftl"
                 to="${escapeXmlAttribute(srcOut)}/${openerClass}.java"/>

    <instantiate from="root/src/app_package/BasePandroidFragmentPresenter.java.ftl"
                 to="${escapeXmlAttribute(srcOut)}/${presenterClass}.java"/>

    <instantiate from="root/res/layout/fragment_base.xml.ftl"
                 to="${escapeXmlAttribute(resOut)}/layout/${layoutName}.xml" />


    <open file="${escapeXmlAttribute(srcOut)}/${fragmentClass}.java"/>
    <open file="${escapeXmlAttribute(resOut)}/layout/${layoutName}.xml"/>


</recipe>
