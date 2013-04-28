/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author Antonio Diaz Arroyo
 * 
 */
public class CompareUtilities {
    	public static List<String> compare(String file1, String file2) {
		List<String> result = new ArrayList<String>();
		try {
			List<String> header1 = readHeader(file1);
			List<String> header2 = readHeader(file2);
			
			Map<String, List<String>> entities1 = readExelMap(file1) ;
			Map<String, List<String>> entities2 = readExelMap(file2) ;
			
			/* compara cabeceras: las 2 primeras filas son iguales. */
			if (header1.size()!=header2.size()) {
				result.add("Las formato de las cabeceras es distinto.");
			} else {
				List<Integer> comparaListas = CompareUtilities.comparaListas(header1, header2);
				if (comparaListas.size()>0) {
					String error = "Las formato de las cabeceras es distinto:\n";
					for (Integer i : comparaListas) {
						error += header1.get(i) + "\t--\t " + header2.get(i) + "\n";
					}
					result.add(error);
				} else {
					Set<String> setEntities1 = entities1.keySet();
					Set<String> setEntities2 = entities2.keySet();
					
					
					Set<String> news = new HashSet<String>();
					news.addAll(setEntities2);
					news.removeAll(entities1.keySet());
					for (String entity : news) {
						result.add("Entidades nueva:\t[" + entity + "]\n");
					}	
					
					Set<String> deletes = new HashSet<String>();
					deletes.addAll(setEntities1);
					deletes.removeAll(setEntities2);
					for (String entity : deletes) {
						result.add("Entidades borrada:\t[" + entity + "]\n");
					}	
					
					Set<String> intersection = entities1.keySet();
					intersection.retainAll(entities2.keySet());  
					for (String entity : intersection) {
						StringBuilder strb = new StringBuilder("\nEntidad modificada: [" + entity + "]");						
						comparaListas = CompareUtilities.comparaListas(entities1.get(entity), entities2.get(entity));
						if (comparaListas.size()>0) {
							strb.append("  tiene cambios: \n");
							for (Integer i : comparaListas) {
								strb.append(header1.get(i)+ ": [" + entities1.get(entity).get(i) + "][" + entities2.get(entity).get(i) + "]\n");
							}
						} else {
							strb.append(" SIN CAMBIOS");
						}
						result.add(strb.toString());
					}					
				}				
			}
		} catch (IOException e) {
			e.printStackTrace();
			result.add("Error: no se pueden leer los ficheros.");
		} catch (Exception e) {
			e.printStackTrace();
			result.add("Error al comparar los ficheros.\n" + e.getMessage());
		}
		return result;
		
	}
	
	private static Map<String, List<String>> readExelMap(String file) throws IOException {
		InputStream inputStream = new FileInputStream(file);
	    HSSFWorkbook workbook = new HSSFWorkbook(inputStream);	    
	    /* reading file1 */ 
	    HSSFSheet sheet = workbook.getSheetAt(0);
	    Map<String, List<String>> myMap = new HashMap<String, List<String>>();
	    Integer rowCount = 0;
	    for (Row row : sheet) {
	    	if (++rowCount>=3) {
		    	List<String> myRow = new ArrayList<String>();
		    	for (Cell cell : row) {
		    		myRow.add(cell.toString());		    		
		    	}
		    	myMap.put(myRow.get(0), myRow);
	    	}
		}
	    inputStream.close();
	    return myMap;
	}

	/**
	 * Compare two list wich the same size.
	 * @param list1
	 * @param list2
	 * @return
	 */
	private static List<Integer> comparaListas(List<String> list1, List<String> list2) {
		List<Integer> diff = new ArrayList<Integer>();
		for (int i = 0; i < list1.size(); i++) {
			if (!list1.get(i).equalsIgnoreCase(list2.get(i))){
				diff.add(i);
			}
		}
		return diff;
	}

	private static List<String> readHeader(String filePath) throws IOException{
		InputStream inputStream = new FileInputStream(filePath);
	    HSSFWorkbook workbook = new HSSFWorkbook(inputStream);	    
	    /* reading file1 */ 
	    HSSFSheet sheet = workbook.getSheetAt(0);
	    List<String> lista = new ArrayList<String>();
	    Row row = sheet.getRow(3);
    	for (Cell cell : row) {
    		lista.add(cell.toString());		    		
    	}
	    inputStream.close();
	    return lista;
	}

}
