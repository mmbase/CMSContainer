//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.09.11 at 05:02:54 PM CEST 
//

package com.finalist.portlets.playlist.dto;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for musibase_xml_playlist_export element declaration.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;element name=&quot;musibase_xml_playlist_export&quot;&gt;
 *   &lt;complexType&gt;
 *     &lt;complexContent&gt;
 *       &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *         &lt;sequence&gt;
 *           &lt;element ref=&quot;{}playlist&quot;/&gt;
 *         &lt;/sequence&gt;
 *         &lt;attribute name=&quot;version&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}decimal&quot; /&gt;
 *       &lt;/restriction&gt;
 *     &lt;/complexContent&gt;
 *   &lt;/complexType&gt;
 * &lt;/element&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "playlist" })
@XmlRootElement(name = "musibase_xml_playlist_export")
public class MusibaseXmlPlaylistExport {

   @XmlElement(required = true)
   protected Playlist playlist;
   @XmlAttribute(required = true)
   protected BigDecimal version;


   /**
    * Gets the value of the playlist property.
    * 
    * @return possible object is {@link Playlist }
    */
   public Playlist getPlaylist() {
      return playlist;
   }


   /**
    * Sets the value of the playlist property.
    * 
    * @param value
    *           allowed object is {@link Playlist }
    */
   public void setPlaylist(Playlist value) {
      this.playlist = value;
   }


   /**
    * Gets the value of the version property.
    * 
    * @return possible object is {@link BigDecimal }
    */
   public BigDecimal getVersion() {
      return version;
   }


   /**
    * Sets the value of the version property.
    * 
    * @param value
    *           allowed object is {@link BigDecimal }
    */
   public void setVersion(BigDecimal value) {
      this.version = value;
   }

}