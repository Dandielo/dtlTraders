package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

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
	}

	@Override
	public String onSave()
	{
		//save the book id data
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
	public void onFactorise(ItemStack item)
			throws AttributeValueNotFoundException
	{
	}
	
}
