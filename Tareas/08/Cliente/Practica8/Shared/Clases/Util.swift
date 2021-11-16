//
//  Util.swift
//  Practica8
//
//  Created by René Payán Téllez on 15/11/21.
//

import Foundation

struct Util{
    public func hacerPeticion(metodo: String, ruta: String, parametros: Dictionary<String,String>) async->(codigoHTTP: Int, respuesta: String){
        let codigoRespuesta: Int = 200
        let textoRespuesta: String = ""
        let url = URL(string: "http://192.168.1.71:8080/"+ruta)!
        var request = URLRequest(url: url)
        request.httpMethod = metodo
        if(parametros != nil){
            let bodyData:String = ""
            for (parametro, valor) in parametros {
                bodyData = bodyData+(parametro + "="+valor+"&")
            }
            
            request.httpBody = bodyData.data(using: String.Encoding.utf8);
        }
        let peticion = Task{()
            let session = URLSession.shared
            let task = session.dataTask(with: request) { (data, response, error) in

                if let error = error {
                    // Handle HTTP request error
                } else if let data = data {
                    // Handle HTTP request response
                } else {
                    print("no se que paso")
                }
            }
        }
        
        task.resume()
        return(codigoRespuesta, textoRespuesta)
    }
}
