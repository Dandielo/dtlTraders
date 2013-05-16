package net.dandielo.citizens.traders_v3.core.tools;

public class StringTools {

	public static String stackTrace(StackTraceElement[] elements)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("\n");
		builder.append("---\n");
		for ( StackTraceElement element : elements )
			builder.append(element + "\n");
		builder.append("---\n");
		return builder.toString();
	}
}
