package net.dandielo.citizens.traders_v3.traders.patterns;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.patterns.types.Item;
import net.dandielo.citizens.traders_v3.traders.patterns.types.Price;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.avaje.ebeaninternal.server.ddl.DdlGenContext;

public class PatternManager {
	public static PatternManager instance = new PatternManager();
	
	//all loaded patterns
	private HashMap<String, Pattern> patterns = new HashMap<String, Pattern>(); 
	
	//the pattern file and configuration
	protected File patternsFile;
	protected FileConfiguration patternsConfig;
	
	private PatternManager()
	{		
		String fileName = GlobalSettings.getPatternFile();
		String baseDir = "plugins/dtlTraders";

		if ( baseDir.contains("\\") && !"\\".equals(File.separator) ) 
		{
			baseDir = baseDir.replace("\\", File.separator);
		}

		File baseDirectory = new File(baseDir);
		if ( !baseDirectory.exists() ) 
		{
			baseDirectory.mkdirs();
		}

		this.patternsFile = new File(baseDir, fileName);

		if ( !patternsFile.exists() )
		{
			try 
			{
				patternsFile.createNewFile();
			} 
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		this.reload();
	}
	
	public void reload()
	{
		patternsConfig = new YamlConfiguration();
		try 
		{
			patternsConfig.load(patternsFile);
			
			for ( String patternName : patternsConfig.getKeys(false) )
			{
				Pattern pattern = createPattern(patternName, patternsConfig.getString(patternName + ".type"));
				dB.normal(patternName);
				pattern.loadItems(patternsConfig.getConfigurationSection(patternName));
				patterns.put(patternName.toLowerCase(), pattern);
			}
		} 
		catch( Exception e )
		{
			throw new IllegalStateException("Error loading patterns file", e);
		}
	}

	public static Pattern createPattern(String name, String type)
	{
		if ( type.equals("price") )
			return new Price(name);
		if ( type.equals("item") )
			return new Item(name);
		return null;
	}
	
	public static List<Pattern> getPatterns(List<String> names)
	{
		List<Pattern> result = new ArrayList<Pattern>();
		for ( Map.Entry<String, Pattern> e : instance.patterns.entrySet() )
		{
			if ( names.contains(e.getKey()) )
				result.add(e.getValue());
		}
		return result;
	}
}
