public class FailedToWriteToFileException extends Exception{
    FailedToWriteToFileException(String error_msg){
        super(error_msg);
    }
}