package api.musixMatch.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Fichier {
public static void lectureFile(){
		
		for(int y=1 ; y<3; y++){
			File fichier = new File("MusiqueArtisten"+y);
			
			try{
				InputStream ips=new FileInputStream(fichier); 
				InputStreamReader ipsr=new InputStreamReader(ips);
				BufferedReader br=new BufferedReader(ipsr);
				String ligne;
				while ((ligne=br.readLine())!=null){
					String tmp[] = ligne.split("track_id");
					if(tmp.length>1){
						for(int i=0;i<tmp.length;i++){
							String s = tmp[i].split(",")[0];
							if(s!=null){
								//s.substring(2);
								System.out.println(s.substring(2));
							}
							//System.out.println(tmp[i]);
							//System.out.println(tmp[1].substring(2, 20));
						}
					}
					//System.out.println(ligne);
				}
				br.close(); 
			}		
			catch (Exception e){
				System.out.println(e.toString());
			}
		}
		
	}
	
	public static void sauveFichier(String titreFichier, String list){
		final File fichier =new File(titreFichier); 
        try {
            // Creation du fichier
            fichier .createNewFile();
            // creation d'un writer (un �crivain)
            final FileWriter writer = new FileWriter(fichier);
            try {
                writer.write(list);
            } finally {
                // quoiqu'il arrive, on ferme le fichier
                writer.close();
            }
        } catch (Exception e) {
            System.out.println("Impossible de creer le fichier");
        }
	}

}
