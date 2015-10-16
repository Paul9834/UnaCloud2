<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            New Image from Public
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="${createLink(uri: '/services/image/list', absolute: true)}"><i class="fa fa-th-list"></i> Images</a></li>
            <li><a href="${createLink(uri: '/services/image/new', absolute: true)}"><i class="fa fa-file"></i> New Image</a></li>
            <li class="active">Public</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">
    		<div class="col-xs-12">      			
    			<div id="label-message"></div>      
    			<form id="form-create" name="imageNewPublic" action="${createLink(uri: '/services/image/public/copy', absolute: true)}" enctype="multipart/form-data" role="form">
	                        		     
		       		<div class="box box-primary">     
		            	<!-- form start -->
		            	 <div class="box-header">
		            	 	<h5 class="box-title">Step 1: Select the image to copy</h5>
		            	 </div>
		           	 	 <div class="box-body"> 
		                   	 <div class="form-group">
		                         <div class="table-responsive">
	                         	 	<div class="form-group">
	                         	 		<p class="help-block">* Operating System - Image Name - Access protocol</p>
                                        <select name= "image" class="form-control">
                                        <g:each in="${publicImages}" var="image"> 
                                        	<option value="0">--Select a Public Image--</option>
                                            <option  value="${image.id}">${image.operatingSystem.name} - ${image.name} - ${image.accessProtocol}</option>                                                
                                        </g:each>    
                                        </select>                                            
                                 	</div>
                                 </div>
		           			 </div>                       
		                 </div><!-- /.box-body -->		                         	
		       	 	</div>
		       	 	          		     
		       		<div class="box box-primary">  
		            	<div class="box-header">
		            	 	<h5 class="box-title">Step 2: Write a new name for the image</h5>
		            	</div>
		           	 	<div class="box-body">	
		           	 	 	<div class="form-group">
		                       <label>Image Name</label>
		                       <input type="text" class="form-control" name="name" placeholder="Image Name">
		                    </div>  
		           		</div>	
		           		<div class="box-footer"> 
		           			<g:submitButton name="button-submit" class="btn btn-primary" value="Submit" />		
		                </div>	  		
    				</div>   
	       	 	</form>
    		</div>			
    	</div>   	
	</section><!-- /.content -->     
	<asset:javascript src="pages/images.js" />
	<%--<script>$(document).on('ready',function(){ $("#unacloudTable").dataTable();})</script>
--%></body>