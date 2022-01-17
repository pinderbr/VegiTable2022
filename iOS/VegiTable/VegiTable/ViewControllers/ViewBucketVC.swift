//
//  ViewBucketVC.swift
//  VegiTable
//

import UIKit

// implemented to conform to the Downloadable protocol
extension ViewBucketVC: Downloadable {
    func didReceiveData(data: Any) {
        DispatchQueue.main.sync {
            plantList = data as! [Plant]
            plantCollection.reloadData()
        }
    }
}
class ViewBucketVC: UIViewController, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout, ParentChildDelegate {
    func getData(id: Int) {
        print("Testing")
    }

    @IBOutlet weak var plantCollection: UICollectionView!
    let cellReuseId = "PlantCell"
    var currentBucketId: Int = 0
    
    public var containerDelegate: ContainerDelegate?
    let parentVC = ContainerVC()
    
    let plantModel = PlantsModel()
    var plantList: [Plant] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        plantCollection.dataSource = self
        plantCollection.delegate = self
        plantModel.delegate = self
        parentVC.delegate = self
        
        let layout = UICollectionViewFlowLayout()
        layout.minimumLineSpacing = 20
        layout.minimumInteritemSpacing = 20
        plantCollection.collectionViewLayout = layout
        
        plantCollection.backgroundColor = UIColor.init(named: "VT_BgColor")!
        
        if(currentBucketId != 0) {
            plantModel.getPlantsByBucket(id: currentBucketId, url: ApiCalls.getPlants)
        }
    }
    
    @IBAction func refreshData() {
        if(currentBucketId != 0) {
            plantModel.getPlantsByBucket(id: currentBucketId, url: ApiCalls.getPlants)
        }
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return plantList.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = plantCollection.dequeueReusableCell(withReuseIdentifier: cellReuseId, for: indexPath) as! BucketCollectionViewCell
        
        cell.layer.borderWidth = 2
        cell.layer.borderColor = UIColor.init(named: "VT_InputPlaceholder")!.cgColor
        
        let index = indexPath.row
        if(plantList.count > 0) {
            cell.plantNameLbl.text = plantList[index].plantName + " - "
            cell.plantTypeLbl.text = plantList[index].plantType
            cell.phValueLbl.text = String(plantList[index].phMin) + " - " + String(plantList[index].phMax)
            cell.ppmValueLbl.text = String(plantList[index].ppmMin) + " - " + String(plantList[index].ppmMax)
            cell.humidityValueLbl.text = String(plantList[index].humidityMin) + " - " + String(plantList[index].humidityMax)
            cell.temperatureValueLbl.text = String(plantList[index].temperatureMin) + " - " + String(plantList[index].temperatureMax)
            cell.lightValueLbl.text = String(plantList[index].lightMin) + " - " + String(plantList[index].lightMax)
         }
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView,layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: 336, height: 170)
    }
    
    func collectionView(_ collectionView: UICollectionView, didDeselectItemAt indexPath: IndexPath) {
        containerDelegate?.displayNewView(storyboardID: "ViewPlantVC", viewTitle: "View Plant", data: nil)
    }
    
    @IBAction func onAddPlantClick() {
        containerDelegate?.displayNewView(storyboardID: "AddPlantVC", viewTitle: "Add Plant", data: nil)
    }
    
    @IBAction func onEditPlantClick() {
        containerDelegate?.displayNewView(storyboardID: "EditPlantVC", viewTitle: "Edit Plant", data: nil)
    }
    
    @IBAction func onViewDataClick() {
        containerDelegate?.displayNewView(storyboardID: "ViewBucketDataVC", viewTitle: "Bucket Data", data: nil)
    }
    
    
}
