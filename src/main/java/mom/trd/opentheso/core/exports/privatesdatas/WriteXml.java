package mom.trd.opentheso.core.exports.privatesdatas;


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
        endTable(Tablename);
    }
    
    private void startTable(String tableName) {
        xml += "\n";
        xml += "<" + tableName + ">";
    }
    
    private String endTable(String tableName) {
        xml += "\n";
        xml += "</" + tableName + ">";
        return xml;
    }    
    
    private void startLine() {
        xml += "\n";
        xml += "    ";
        xml += "<ligne>";
    }
    
    private void endLine() {
        xml += "\n";
        xml += "    ";
        xml += "</ligne>";
    }    
    
    private void writeLine(String colomne, String value) {
        xml += "\n";
        xml += "        ";
        xml += "<" + colomne + ">";
        xml += "\n";
        xml += "            ";        
        xml += "<value>";
        xml += value;
        xml += "</value>";
        xml += "\n";
        xml += "        ";
        xml += "</" + colomne + ">";
    }
    
    public String writeHead(){
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        return xml;
    }
    
    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }
    
}