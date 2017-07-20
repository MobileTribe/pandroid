<?xml version="1.0" encoding="UTF-8"?><recipe>
  <instantiate from="root/src/app_package/BasePandroidFragment.java.ftl" to="${escapeXmlAttribute(srcOut)}/${fragmentClass}.java"/>
  <instantiate from="root/src/app_package/BasePandroidFragmentOpener.java.ftl" to="${escapeXmlAttribute(srcOut)}/${openerClass}.java"/>
  <instantiate from="root/src/app_package/BasePandroidFragmentPresenter.java.ftl" to="${escapeXmlAttribute(srcOut)}/${presenterClass}.java"/>
  <instantiate from="root/res/layout/fragment_base.xml" to="${escapeXmlAttribute(resOut)}/layout/${layoutName}.xml"/>
  <open file="${escapeXmlAttribute(srcOut)}/${fragmentClass}.java"/>
</recipe>
