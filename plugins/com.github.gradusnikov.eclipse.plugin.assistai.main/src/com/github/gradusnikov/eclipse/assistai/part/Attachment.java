package com.github.gradusnikov.eclipse.assistai.part;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.graphics.ImageData;

/**
 * Represents an attachment to a chat message, e.g. an image or content from a
 * file.
 */
public interface Attachment
{
    String toChatMessageContent();

    String toMarkdownContent();

    ImageData getImageData();

    void accept( UiVisitor visitor );

    public interface UiVisitor
    {
        void add( ImageData preview, String caption );
    }

    abstract class BaseAttachment implements Attachment
    {
        @Override
        public String toChatMessageContent()
        {
            return null;
        }

        @Override
        public String toMarkdownContent()
        {
            return null;
        }

        @Override
        public ImageData getImageData()
        {
            return null;
        }
    }

    /**
     * Represents a context item with a file name, start and end line numbers, and
     * selected content.
     */
    public class FileContentAttachment extends BaseAttachment
    {
        private static final LocalResourceManager resourceManager = new LocalResourceManager( JFaceResources.getResources() );
        private static ImageData icon;
        
        static {
            ImageDescriptor iconDescriptor = ImageDescriptor.createFromFile( FileContentAttachment.class,
                    "/icons/folder.png" );
            icon = iconDescriptor.getImageData( 100 );
        }
        
        private final String fileName;
        private final int lineNumberStart;
        private final int lineNumberEnd;
        private final String selectedContent;

        public FileContentAttachment(String fileName, int lineNumberStart, int lineNumberEnd, String selectedContent)
        {
            this.fileName = fileName;
            this.lineNumberStart = lineNumberStart;
            this.lineNumberEnd = lineNumberEnd;
            this.selectedContent = selectedContent;
        }

        public String getFileName()
        {
            return fileName;
        }

        public int getLineNumberStart()
        {
            return lineNumberStart;
        }

        public int getLineNumberEnd()
        {
            return lineNumberEnd;
        }

        public String getSelectedContent()
        {
            return selectedContent;
        }

        @Override
        public String toChatMessageContent()
        {
            return String.format( """
                    === Context
                    File: %s
                    Lines: %s
                    %s
                    ===
                    """, fileName, lineNumberStart > 0 ? lineNumberStart + "-" + lineNumberEnd : "unknown",
                    selectedContent );
        }

        @Override
        public String toMarkdownContent()
        {
            String[] path = fileName.split( "/" );
            return String.format( "File %s, Lines %d-%d", path[path.length - 1], lineNumberStart, lineNumberEnd );
        }

        @Override
        public void accept( UiVisitor visitor )
        {
            visitor.add( icon, String.format( "File: %s, Line: %d-%d", fileName, lineNumberStart, lineNumberEnd ) );
        }
    }

    public class ImageAttachment extends BaseAttachment
    {
        private final ImageData image;
        private final ImageData preview;

        public ImageAttachment(ImageData image, ImageData preview)
        {
            this.image = image;
            this.preview = preview;
        }

        public ImageData getImageData()
        {
            return image;
        }

        @Override
        public void accept( UiVisitor visitor )
        {
            visitor.add( preview, null );
        }
    }
}
