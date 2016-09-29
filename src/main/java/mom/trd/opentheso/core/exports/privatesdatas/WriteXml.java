package mom.trd.opentheso.core.exports.privatesdatas;

/**
 *
 * @author antonio.perez
 */

import java.util.ArrayList;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;

public class WriteXml {

    private String xml = "";
    
    
    public WriteXml() {
    }
    
    public void WriteIntoXML(ArrayList<Table> DataTable, String Tablename) {
        startTable(Tablename);
        for (Table user : DataTable) {
            startLine();
            for (LineOfData lineOfData : user.getLineOfDatas()) {
                writeLine(lineOfData.getColomne(), lineOfData.getValue());
            }
            endLine();
        }
        endTable();
    }
    
    private void startTable(String tableName) {
        xml += "\n";
        xml += "    ";
        xml += "<table nom =\"" + tableName + "\">";
    }
    
    private String endTable() {
        xml += "\n";
        xml += "    ";
        xml += "</table>";
        return xml;
    }    
    
    private void startLine() {
        xml += "\n";
        xml += "        ";
        xml += "<ligne>";
    }
    
    private void endLine() {
        xml += "\n";
        xml += "        ";
        xml += "</ligne>";
    }    
    
    private void writeLine(String colomne, String value) {
        xml += "\n";
        xml += "            ";
        xml += "<" + colomne + ">";
        xml += value;
        xml += "</" + colomne + ">";
    }
    
    public void writeHead(){
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        xml += "\n";
    }
    
    public void start(){
        xml += "<tables>";
    }
    
    public void end(){
        xml += "\n";
        xml += "</tables>";
    }    
    
    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }
    
}