/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.tree;

public interface TreeInfo {

   void expand(Object o);


   void collapse(Object o);


   boolean isOpen(Object o);

}
