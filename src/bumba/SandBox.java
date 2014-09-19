package bumba;

import bumba.database.core.*;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by emcee on 8/26/13.
 */
public class SandBox {
    public static void main(String[] args) {
        Connection c = null;

        try {
            c = DataConnector.getTestDatabaseConnection();
            Database db = Database.getDatabase(c);
            Schema schema = db.getSchema("public");
            //createVelocityTemplates(schema);
            //printSomeStuff(table);
            db.setKeyReferences(c);
            ////System.out.println(prettyPrint(table.toString()));
            //printReferences(schema.getTableList());
            //printUniqueColumns(schema.getTableList());
            for (Schema theSchema :db.getSchemas()){
                createVelocityTemplates(theSchema);
            }
            createVelocityTemplates(schema);
            //identifyPotentialNonPrimaryIds(schema);
            //identifyPotentialUnlinkedColumns(schema);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public static void identifyPotentialNonPrimaryIds(Schema schema) {
        for (Table table : schema.getTableList()) {
            if (!table.getTableType().equals("TABLE")) {
                continue;
            }
            for (Column column : table.getColumns()) {
                if (column.getColumnName().equals("id") && !column.isPrimaryKey()) {
                    //System.out.println(column.getCanonicalName());
                    System.out.format("ALTER TABLE %s ADD PRIMARY KEY (%s);\n", table.getCanonicalName(), column.getColumnName());
                }
            }
        }
    }

    public static List<Column> identifyPotentialUnlinkedColumns(Schema schema) {
        // get all fields
        List<Column> potentialKeys = new ArrayList<>();
        //List<Column> allColumns = new ArrayList<>();
        for (Table table : schema.getTableList()) {
            // get rid of it if it isn't a real table
            if (!table.getTableType().equals("TABLE")) {
                continue;
            }
            for (Column column : table.getColumns()) {
                if (column.getColumnName().endsWith("_id") && !column.isReference()) {
                    // potential, check for table
                    Table potentialTable = null;
                    try {

                        potentialTable = schema.getTable(column.getColumnName().replace("_id",""));

                        //System.out.format("potential match: %s to %s\n", column.getCanonicalName(), potentialTable.getCanonicalName());
                        String alterStatement = "ALTER TABLE %s ADD CONSTRAINT fk_%s_%s FOREIGN KEY (%s) REFERENCES %s (%s) MATCH FULL;\n";
                        System.out.format(alterStatement, column.getOwningTable().getCanonicalName(), table.getTableName(), column.getColumnName(), column.getColumnName(), potentialTable.getCanonicalName(), potentialTable.getPrimaryKey().getColumnName());
                    } catch (Exception e) {
                        // no match found, oh well
                        System.err.format("No match found: %s\n", column.getCanonicalName());
                    }
                    potentialKeys.add(column);
                }
            }
        }
        return potentialKeys;
    }

    public static void printReferences(List<Table> tableList) {
        for (Table table : tableList) {
            for (Column column : table.getColumns(true)) {
                if (column.isReference()) {
                    //System.out.println(String.format("%s.%s references %s(%s)", column.getOwningTable().getTableName(), column.getColumnName(), column.getReferencedColumn().getOwningTable().getTableName(), column.getReferencedColumn().getColumnName()));
                } else if (column.isReferenced()) {
                    for (Column innerColumn : column.getReferencingColumns()) {
                        //System.out.println(String.format("%s.%s is referenced by column %s.%s", column.getOwningTable().getTableName(), column.getColumnName(), innerColumn.getOwningTable().getTableName(), innerColumn.getColumnName()));
                    }
                }
            }
        }
    }

    public static void printUniqueColumns(List<Table> tables) {
        for (Table table : tables) {
            for (Column column : table.getColumns()) {
                if (column.isUnique()) {
                    //System.out.println(String.format("%s.%s is unique", column.getOwningTable().getTableName(), column.getColumnName()));
                }
            }
        }
    }

    public static void printResultSet(ResultSet rs) {
        try {
            while (rs.next()) {
                ResultSetMetaData metadata = rs.getMetaData();
                for (int i = 1; i <= metadata.getColumnCount(); i++) {
                    //System.out.println(metadata.getColumnName(i) + ": " + rs.getObject(i));
                }
                //System.out.println(" ----- ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createVelocityTemplates(Schema schema) throws Exception {
        for (Table table : schema.getTableList()) {
            if (table.getTableType().equals("TABLE")) {
                createVelocityTemplate(table);
            }

        }

    }

    public static void createVelocityTemplate(Table table) throws Exception {
        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", "templates\\DataObject");
        Velocity.init(p);
        VelocityContext context = new VelocityContext();
        //System.out.println("table start: " + table.getTableName());
        //System.out.println("primary: " + table.getPrimaryKey());
        //System.out.println(table.getTableType());
        for (Column column : table.getColumns()) {
            //System.out.println(column.getColumnName());
            //System.out.println(column.getJavaType());

        }
        for (Column column : table.getForeignKeyReferences()) {
            //System.out.println("foreign key:" + column.getOwningTable() + "." + column.getColumnName());
        }
        context.put("do", table);
        context.put("pk", table.getPrimaryKey());

        Template template = Velocity.getTemplate("DataObject.vm");
        System.out.format("Processing %s for template %s\n", table.getTableName(), template.toString());
        PrintWriter printWriter = new PrintWriter("src/generated/" + table.getCamelCaseName(true) + ".java", "UTF-8");
        template.merge(context, printWriter);
        printWriter.flush();
        printWriter.close();
        System.out.format("Completed processing %s for template %s\n", table.getTableName(), template.toString());
    }
}
