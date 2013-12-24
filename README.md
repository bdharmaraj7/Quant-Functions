AWS-S3-Backup
=============

This is a simple Java client that allows the user to upload files to Amazon's S3. 


Setup Files
-----------

The process employs two files saved locally:

(1) "awskeys.json" which holds your AWS Access ID and Secret Key in the following format:

{"AWSKey1": ["AWS Access ID goes here"], "AWSKey2": ["AWS Secret Key goes here"]}

(2) "s3files.xml" which holds the files/folders that need to be backed up to S3 in the following format:

<s3backup>
  <bucket name="bucket1">
    <file>/home</file"
  </bucket>
  <bucket name="bucket2">
    <file>/home/bdharmaraj/tech resources</file>
    <file>/home/bdharmaraj/research_paper.PDF</file>
    <file>/home/bdharmaraj/curriculum.docx</file>
  </bucket>
</s3backup>

The <file> tag can hold file or folder names. 

If a specified bucket is not present in your S3 account, it will be created before uploading its files.


(3) InitialSetup.java will create the two setup files. Before calling InitialSetup.java make sure to change the values in the main method to reflect your AWS Access ID, AWS Secret Key and path to files/folders that you would want to upload to S3. 

You may choose to create/populate the setup files through another method of your choice.

Backup Process
--------------

(1) Once the two setup files are created, start the backup process by calling BackupMain (java BackupMain)









