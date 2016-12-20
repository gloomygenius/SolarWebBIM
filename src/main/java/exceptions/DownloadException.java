package exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DownloadException extends Exception{
    public DownloadException(String message){
        super(message);
    }
    public DownloadException(Throwable e){
        super(e);
    }
    public DownloadException(String message, Throwable e){
        super(message,e);
    }
}