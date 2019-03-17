package cs.cooble;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static final String SAMPLE_XLSX_FILE_PATH = "D:\\Datasheets\\mypc\\Instructions.xlsx";

    public static void main(String[] args) throws Exception {

        System.out.println("Loading excel file "+SAMPLE_XLSX_FILE_PATH+"\n");
       Workbook workbook;
        try {
            workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));

        }catch (Exception e){
           System.err.println("Cannot open "+SAMPLE_XLSX_FILE_PATH);
            System.err.println("Please close file that file");
            return;
        }
        Sheet mainSheet = workbook.getSheet("MAIN");
        Sheet addressSheet = workbook.getSheet("ADDRESS");
        Sheet translateSheet = workbook.getSheet("TRANSLATE");
        Sheet pinoutSheet = workbook.getSheet("PINOUT");
        Sheet negationSheet = workbook.getSheet("NEGATION");
        InstructionList instructionList = new InstructionList();
        Pinout pinout = new Pinout();

        loadPINOUT(pinoutSheet, pinout);
        loadNEGATION(negationSheet, pinout);
        loadTRANSLATE(translateSheet, pinout);

        loadMAIN(mainSheet, instructionList, pinout);
        loadADDRESS(addressSheet, instructionList);
        workbook.close();


        BinaryEngine engine = new BinaryEngine(instructionList, pinout);
        Map<Integer, Integer> map = engine.process();

        Saver saver = new Saver();
        File file = new File("D:\\Datasheets\\mypc\\wholerom.txt");
        saver.saveToWholeFile(map, file);
        for (int i = 0; i < 3; i++) {
            saver.saveOneRom(i,map,new File("D:/Datasheets/mypc/"));

        }
        System.out.println("Saved to " + file.getAbsolutePath());
    }

    private static void loadTRANSLATE(Sheet sheet, Pinout pinout) {
        int ignore = 2;
        Map<Integer, Integer> map = new HashMap<>();
        for (Row row : sheet) {
            if (ignore != 0) {
                ignore--;
                continue;
            }
            if (row.getPhysicalNumberOfCells() != 0) {
                map.put((int) row.getCell(0).getNumericCellValue(), (int) row.getCell((1)).getNumericCellValue());
            }
        }
        pinout.translate(map);
    }

    private static void loadPINOUT(Sheet sheet, Pinout pinout) {
        int ignore = 1;
        List<Integer> indexes = null;
        List<String> pinNames = new ArrayList<>();
        for (Row row : sheet) {
            if (ignore != 0) {
                ignore--;
                continue;
            }
            if (indexes == null) {
                indexes = new ArrayList<>();
                for (Cell cell : row)
                    indexes.add((int) cell.getNumericCellValue());
            } else
                for (Cell cell : row)
                    pinNames.add(cell.getStringCellValue());


        }
        for (int i = 0; i < pinNames.size(); i++)
            pinout.setPin(pinNames.get(i), indexes.get(i));
    }
  private static void loadNEGATION(Sheet sheet, Pinout pinout) {
      int ignore = 1;
        List<Boolean> negates = new ArrayList<>();
        List<String> pinNames =null;
        for (Row row : sheet) {
            if (ignore != 0) {
                ignore--;
                continue;
            }
            if (pinNames == null) {
                pinNames = new ArrayList<>();
                for (Cell cell : row)
                    pinNames.add(cell.getStringCellValue());
            } else
                for (Cell cell : row)
                    negates.add(cell.getNumericCellValue()==1);

        }
      for (int i = 0; i < pinNames.size(); i++) {
          pinout.setPin(pinNames.get(i),negates.get(i));
      }
    }

    private static void loadADDRESS(Sheet sheet, InstructionList instructionList) {
        DataFormatter dataFormatter = new DataFormatter();
        for (Row row : sheet) {
            if (row.getPhysicalNumberOfCells() != 0) {
                String instructionName = row.getCell(0).getStringCellValue();
                int address = Integer.decode("0x" + dataFormatter.formatCellValue(row.getCell(1)));
                instructionList.getInstruction(instructionName).setIndex(address);
            }

        }
    }

    private static void loadMAIN(Sheet sheet, InstructionList instructionList, Pinout pinout) {
        List<String> columnNames = new ArrayList<>();
        List<Integer> collumnAdres = new ArrayList<>();
        List<String> pinNames = pinout.getNames();

        DataFormatter dataFormatter = new DataFormatter();

        boolean skip = true;
        boolean columnSet = false;
        InstructionList.Instruction instruction = null;

        for (Row row : sheet) {
            if (skip) {//skip first line
                skip = false;
                continue;
            }
            if (!columnSet) {
                for (Cell cell : row) {
                    columnNames.add(cell.getStringCellValue());
                    collumnAdres.add(cell.getColumnIndex());
                }
                columnSet = true;
                continue;
            }


            List<String> cells = new ArrayList<>();


            for (Cell cell : row) {
                cells.add(dataFormatter.formatCellValue(cell));
            }
            if(cells.size()==0)
                continue;
            int step = Integer.parseInt(cells.get(1));
            if (cells.size() == 0)
                break;
            if (!(cells.get(0) == null || cells.get(0).equals(""))) {//new instruction
                instruction = instructionList.addInstruction(cells.get(0));
            }
            for (Cell cell : row) {
                if (cell == null)
                    continue;
                if (cell.getColumnIndex() < 2)//skip instruction name + t
                    continue;
                if (collumnAdres.contains(cell.getColumnIndex())) {
                    Boolean b = false;
                    if (dataFormatter.formatCellValue(cell).equals("1"))
                        b = true;
                    else if (dataFormatter.formatCellValue(cell).equalsIgnoreCase("X"))
                        b = null;
                    String pinName = columnNames.get(collumnAdres.indexOf(cell.getColumnIndex()));
                    if (pinNames.contains(pinName))
                        instruction.set(step, pinName, b);
                    else instruction.setFlag(step, pinName, b);
                }


            }
        }
    }
}
