//
//  ArchivedPlantsVC.swift
//  VegiTable
//

import UIKit

class ArchivedPlantsVC: UIViewController, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    
    @IBOutlet weak var archivedPlantsCollection: UICollectionView!
    @IBOutlet weak var btnFilter: UIView!
    let cellReuseId = "PlantCell"

    override func viewDidLoad() {
        super.viewDidLoad()
        
        archivedPlantsCollection.dataSource = self
        archivedPlantsCollection.delegate = self
        
        let layout = UICollectionViewFlowLayout()
        layout.minimumLineSpacing = 30
        layout.minimumInteritemSpacing = 30
        archivedPlantsCollection.collectionViewLayout = layout
        
        archivedPlantsCollection.backgroundColor = UIColor.init(named: "VT_BgColor")!
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 12
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = archivedPlantsCollection.dequeueReusableCell(withReuseIdentifier: cellReuseId, for: indexPath) as! ArchivedPlantsCollectionViewCell
        cell.layer.borderWidth = 2
        cell.layer.borderColor = UIColor.init(named: "VT_InputPlaceholder")!.cgColor
        cell.cellSetup()
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: 320, height: 100)
    }

}
