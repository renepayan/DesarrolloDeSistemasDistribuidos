//
//  Foto.swift
//  Practica8
//
//  Created by René Payán Téllez on 15/11/21.
//

import Foundation
struct Foto: Codable{
    var idFoto: Int32
    var imagen: [UInt8]
    var tipo: String

    public func toJSON()->[String:Any]{
        
        let data = NSData(bytes: imagen, length: imagen.count)
        let base64Data = data.base64EncodedData(options: NSData.Base64EncodingOptions.endLineWithLineFeed)
        let respuesta:[String:Any] = [
            "idFoto": self.idFoto,
            "imagen": base64Data,
            "tipo": self.tipo
        ]
        return respuesta
    }
}
