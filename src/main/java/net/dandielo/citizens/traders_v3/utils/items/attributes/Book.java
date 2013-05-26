package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

/**
 * Holds data about books that a trader has in his store
 * @author dandielo
 * 
 */
@Attribute(name="Book", key="bk")
public class Book extends ItemAttr {
	//book id used for loading and saving
	private String bookId;
	
	//for WrittenBook
	private String author;
	private String title;
	
	//for each book item
	private List<String> pages;
	
	public Book(String key)
	{
		super(key);
		
		//set defaults
		author = null;
		title = null;
		
		//empty pages list
		pages = new ArrayList<String>();
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException
	{
		//get the saved book id (used to retrieve data from books.yml)
		bookId = data;
		
		//load the book from file
		author = books.getString(bookId + ".author");
		title = books.getString(bookId + ".title");
		pages.addAll(books.getStringList(bookId + ".pages"));
	}

	@Override
	public String onSave()
	{
		//save the book
		books.set(bookId + ".author", author);
		books.set(bookId + ".title", title);
		books.set(bookId + ".pages", pages);
		
		//save the file
		save();
		
		//return the books id
		return bookId;
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException
	{
		if ( !(item.getItemMeta() instanceof BookMeta) ) throw new InvalidItemException();
		BookMeta book = (BookMeta) item.getItemMeta();
		
		//for written books set the title and author
		if ( item.getType().equals(Material.WRITTEN_BOOK) )
		{
			book.setAuthor(author);
			book.setTitle(title);
		}
		
		//set all pages
		book.setPages(pages);
		
		//set the meta
		item.setItemMeta(book);
	}

	@Override
	public void onFactorize(ItemStack item)
			throws AttributeValueNotFoundException
	{
		if ( !(item.getItemMeta() instanceof BookMeta) ) throw new AttributeValueNotFoundException();
		
		//get the book information
		BookMeta book = (BookMeta) item.getItemMeta();
		
		//get title and author
		author = book.getAuthor();
		title = book.getTitle();
		
		//get pages
		pages.addAll(book.getPages());
		
		//generate an Id 
		bookId = title.replace(" ", "_") + new Random().nextInt(1000);
	}
	
	/**
	 * Static constructor
	 */
	static
	{
		try
		{
			loadBooks();
		}
		catch( Exception e )
		{
			Debugger.high("Loading books failed");
		}
	}
	
	/**
	 * The yaml configuration that stores all books 
	 */
	private static FileConfiguration books;
	private static File booksFile;
	
	/**
	 * YamlStorageFile loading
	 * @throws Exception 
	 */
	public static void loadBooks() throws Exception
	{
		String fileName = "books.yml";
		String filePath = "plugins/dtlTraders";
		
		//check the base directory
		File baseDirectory = new File(filePath);
		if ( !baseDirectory.exists() ) 
			baseDirectory.mkdirs();
		
		booksFile = new File(filePath, fileName);
		//if the file does not exists
		if ( !booksFile.exists() )
		{
			//create the file
			booksFile.createNewFile();
		}
		
		//create the books file configuration
		books = new YamlConfiguration();
		
		//load the file as yaml
		books.load(booksFile);
	}
	
	public static void save()
	{
		try
		{
			books.save(booksFile);
		}
		catch( IOException e )
		{
			Debugger.high("Could not save the books file");
		}
	}
}

