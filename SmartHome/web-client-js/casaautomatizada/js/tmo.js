(function() {
	window.Main = {};
	Main.Page = (function() {

		var mosq = null;

		function Page() {
		    var _this = this;
			mosq = new Mosquitto();

			var lamAtiva = "0";
			window.__INITIAL_STATE__ = {
	      			value  : "10",
	      			value2  : "10",
	    	};

            $('#ligarlampada').hide();
   		    
   		    $('#desligarlampada').hide();

            $('#ligaralarme').hide();
   		    
   		    $('#desligaralarme').hide();

            $('#abrirporta').hide();
   		    
   		    $('#fecharporta').hide();


            $("#connect-button").click(function () {
            	
            	var url = $('#hostInput').val();
			    
			    return _this.connect(url);
			    
			});  
    
	
			$('#disconnect-button').click(function() {
				return _this.disconnect();
			});
			$('#subscribe-button').click(function() {
				return _this.subscribe();
			});
			$('#unsubscribe-button').click(function() {
				return _this.unsubscribe();
			});
			
			
			$('#ligarlampada').click(function() {
				var payload = "1";  
				var TopicPublish = document.getElementById("pub-subscribe-text").value;					
				mosq.publish(TopicPublish, payload, 0);
			});

			
			$('#desligarlampada').click(function() {
				var payload = "7";  
				var TopicPublish = document.getElementById("pub-subscribe-text").value;				
				mosq.publish(TopicPublish, payload, 0);
			});
            
            $('#ligaralarme').click(function() {
				var payload = "3";  
				var TopicPublish = document.getElementById("pub-subscribe-text").value;					
				mosq.publish(TopicPublish, payload, 0);
			});

            $('#desligaralarme').click(function() {
				var payload = "8";  
				var TopicPublish = document.getElementById("pub-subscribe-text").value;				
				mosq.publish(TopicPublish, payload, 0);
			});


            $('#abrirporta').click(function() {
				var payload = "5";  
				var TopicPublish = document.getElementById("pub-subscribe-text").value;					
				mosq.publish(TopicPublish, payload, 0);
			});

            $('#fecharporta').click(function() {
				var payload = "9";  
				var TopicPublish = document.getElementById("pub-subscribe-text").value;				
				mosq.publish(TopicPublish, payload, 0);
			});

			mosq.onconnect = function(rc){

				var topic = $('#pub-subscribe-text')[0].value;

				$("#connectionStatus").replaceWith("<h4 class='my-0 font-weight-normal' id='connectionStatus'>Status da conexão com o Broker: CONECTADO !!!</h4>")
				
				 $('#ligarlampada').show();
				 $('#desligarlampada').hide();
                 $('#ligaralarme').show();
				 $('#desligaralarme').hide();
                 $('#abrirporta').show();
				 $('#fecharporta').hide();

				mosq.subscribe(topic, 0);
				
				mosq.subscribe("casa/quarto/temperatura/dados", 0);
				
			};
			mosq.ondisconnect = function(rc){
				var p = document.createElement("p");
				var url = "ws://iot.eclipse.org/ws";
				
				p.innerHTML = "A conexão com o broker foi perdida";
				$("#debug").append(p);				
				mosq.connect(url);
			};
			mosq.onmessage = function(topic, payload, qos){
				
				window.value = payload.substring(13, 17);
				window.value2 = payload.substring(36, 42);

				//escreve o estado do output conforme informação recebida
				if (payload == '1') {
				    $("#lampada").replaceWith("<img id='lampada' src='images/lampada_ligada.png' class='mx-auto d-block'  width='150' height='150'></image>");
					$('#ligarlampada').hide();
   				    $('#desligarlampada').show();
 

				} else if(payload == '7') {

				    $("#lampada").replaceWith("<img id='lampada' src='images/lampada_desligada.png' class='mx-auto d-block'  width='150' height='150'></image>");
					$('#ligarlampada').show();
   				    $('#desligarlampada').hide();
  

				} else if(payload == '3') {

                    $("#alarme").replaceWith("<img id='alarme' src='images/alarme_ligado.png' class='mx-auto d-block'  width='150' height='150'></image>");
					$('#ligaralarme').hide();
   				    $('#desligaralarme').show();
                    console.log("Alarme Ligado.")
  
   		    
   		    				
				} else if(payload == '8') {
                    
                    $("#alarme").replaceWith("<img id='alarme' src='images/alarme_desligado.png' class='mx-auto d-block'  width='150' height='150'></image>");
					$('#ligaralarme').show();
   				    $('#desligaralarme').hide();
					console.log("Alarme Desligado.")				
		
				} else if(payload == '5') {
					$("#porta").replaceWith("<img id='porta' src='images/porta_aberta.png' class='mx-auto d-block'  width='150' height='150'></image>");
					$('#abrirporta').hide();
   				    $('#fecharporta').show();
					
					console.log("Porta Aberta.")				
		
				} else if(payload == '9') {
					$("#porta").replaceWith("<img id='porta' src='images/porta_fechada.png' class='mx-auto d-block'  width='150' height='150'></image>");
					$('#abrirporta').show();
   				    $('#fecharporta').hide();
					console.log("Porta Fechada.")				
				} 
				
			};
		}
		Page.prototype.connect = function(host){
			
			mosq.connect(host);
		};
		Page.prototype.disconnect = function(){
			mosq.disconnect();
		};
		Page.prototype.subscribe = function(){
			var topic = $('#sub-topic-text')[0].value;
			mosq.subscribe(topic, 0);
		};
		Page.prototype.unsubscribe = function(){
			var topic = $('#sub-topic-text')[0].value;
			mosq.unsubscribe(topic);
		};
		
		return Page;
	})();
	$(function(){
		return Main.controller = new Main.Page;
	});
}).call(this);

